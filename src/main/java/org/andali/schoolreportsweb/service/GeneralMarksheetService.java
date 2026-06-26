package org.andali.schoolreportsweb.service;

import jakarta.transaction.Transactional;
import org.andali.schoolreportsweb.dto.GeneralMarksheetDashboardDTO;
import org.andali.schoolreportsweb.dto.GeneralMarksheetSummaryDTO;
import org.andali.schoolreportsweb.model.*;
import org.andali.schoolreportsweb.model.enums.ExamType;
import org.andali.schoolreportsweb.model.enums.MarksheetStatus;
import org.andali.schoolreportsweb.model.enums.Term;
import org.andali.schoolreportsweb.repository.*;
import org.andali.schoolreportsweb.utils.IncompleteMarksheetException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeneralMarksheetService {
    private final GeneralMarksheetRepository generalMarksheetRepository;
    private final MarksheetRepository marksheetRepository;
    private final SchoolSubjectRepository schoolSubjectRepository;


    public GeneralMarksheetService(GeneralMarksheetRepository generalMarksheetRepository, GeneralStudentResultRepository generalStudentResultRepository, SubjectResultRepository subjectResultRepository, MarksheetRepository marksheetRepository, SchoolSubjectRepository schoolSubjectRepository) {
        this.generalMarksheetRepository = generalMarksheetRepository;
        this.marksheetRepository = marksheetRepository;
        this.schoolSubjectRepository = schoolSubjectRepository;
    }

    @Transactional
    public void createGeneralMarksheet(SchoolClass schoolClass, Term term, ExamType examType) {
        // check if a similar general marksheet for the same exam and class exits to avoid duplicated
        if (generalMarksheetRepository
                .existsBySchoolClassAndTermAndExamType(schoolClass, term, examType)) {
            throw new IllegalStateException(
                    "General marksheet already exists!"
            );
        }

        List<Marksheet> subjectSheets = marksheetRepository
                .findBySchoolClassAndTermAndExamTypeAndStatus(
                        schoolClass,
                        term,
                        examType,
                        MarksheetStatus.SUBMITTED // make sure only submitted marksheets are used
                );

        // check is all marksheets for all class subjects have been submitted
        long submittedCount = subjectSheets.size();
        long expectedCount = schoolSubjectRepository.countBySchoolClass(schoolClass);

        if (submittedCount < expectedCount) {
            throw new IncompleteMarksheetException(
                    expectedCount - submittedCount
            );
        }

        // group marks by student
        Map<Student, List<StudentMark>> marksByStudent =
                subjectSheets.stream()
                        .flatMap(ms -> ms.getStudentMarks().stream())
                        .collect(Collectors.groupingBy(StudentMark::getStudent));

        // build a general student result
        List<GeneralStudentResult> results = new ArrayList<>();

        for (var entry : marksByStudent.entrySet()) {
            Student student = entry.getKey();
            List<StudentMark> marks = entry.getValue();

            int total = marks.stream()
                    .mapToInt(StudentMark::getScore)
                    .sum();

            double average = marks.isEmpty()
                    ? 0.0
                    : total /  (double) marks.size();

            GeneralStudentResult gsr = new GeneralStudentResult();
            gsr.setStudent(student);
            gsr.setTotalMarks(total);
            gsr.setAverageMarks(average);

            List<SubjectResult> subjectResults =
                    marks.stream().map(sm -> {
                        SubjectResult sr = new SubjectResult();
                        sr.setSchoolSubject(sm.getMarksheet().getSchoolSubject());
                        sr.setScore(sm.getScore());
                        sr.setGrade(sm.getGrade());
                        return sr;
                    }).toList();
            gsr.setSubjectResults(subjectResults);

            results.add(gsr);
        }

        // assign positions
        results.sort(
                Comparator.comparingInt(GeneralStudentResult::getTotalMarks)
                        .reversed()
        );

        int position = 1;
        for (int i =0; i < results.size(); i++) {
            if (i > 0 &&
                results.get(i).getTotalMarks() <
                        results.get(i-1).getTotalMarks()) {
                position++;
            }
            results.get(i).setPosition(position);
        }

        // save the general marksheet
        GeneralMarksheet generalMarksheet = new GeneralMarksheet();
        generalMarksheet.setSchoolClass(schoolClass);
        generalMarksheet.setTerm(term);
        generalMarksheet.setExamType(examType);
        generalMarksheet.setGeneratedAt(LocalDateTime.now());

        results.forEach(gsr -> gsr.setGeneralMarksheet(generalMarksheet));
        generalMarksheet.setResults(results);
        generalMarksheetRepository.save(generalMarksheet);
    }

    public List<GeneralMarksheetSummaryDTO> getLandingRows() {
        return generalMarksheetRepository.fetchLandingRows();
    }

    public GeneralMarksheetDashboardDTO getDashboardStats() {
        return generalMarksheetRepository.fetchDashboardStats();
    }
}
