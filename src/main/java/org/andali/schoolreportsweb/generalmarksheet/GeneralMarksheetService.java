package org.andali.schoolreportsweb.generalmarksheet;

import jakarta.transaction.Transactional;
import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.MarksheetStatus;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.marksheet.exception.IncompleteMarksheetException;
import org.andali.schoolreportsweb.marksheet.Marksheet;
import org.andali.schoolreportsweb.marksheet.MarksheetRepository;
import org.andali.schoolreportsweb.marksheet.StudentMark;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.student.Student;
import org.andali.schoolreportsweb.subject.SchoolSubjectRepository;
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

    public GeneralMarksheetService(GeneralMarksheetRepository generalMarksheetRepository,
                                   GeneralStudentResultRepository generalStudentResultRepository,
                                   SubjectResultRepository subjectResultRepository,
                                   MarksheetRepository marksheetRepository,
                                   SchoolSubjectRepository schoolSubjectRepository) {
        this.generalMarksheetRepository = generalMarksheetRepository;
        this.marksheetRepository = marksheetRepository;
        this.schoolSubjectRepository = schoolSubjectRepository;
    }

    /**
     * Compiles and generates a GeneralMarksheet for a class, term, and exam type.
     * This compiles individual subject marksheets into a single class-wide sheet,
     * calculates total scores, averages, ranks students, and snapshots results
     * to protect against future modifications.
     *
     * @param schoolClass the class to compile marksheets for
     * @param term the term of the marksheet
     * @param examType the exam type (e.g. BOT, MID, EOT)
     * @throws IllegalStateException if a general marksheet already exists
     * @throws IncompleteMarksheetException if not all subjects for the class are graded/submitted
     */
    @Transactional
    public void createGeneralMarksheet(SchoolClass schoolClass, Term term, ExamType examType) {
        // check if a similar general marksheet for the same exam and class exits to avoid duplicates
        if (generalMarksheetRepository
                .existsBySchoolClassAndTermAndExamType(schoolClass, term, examType)) {
            throw new IllegalStateException("General marksheet already exists!");
        }

        List<Marksheet> subjectSheets = marksheetRepository
                .findBySchoolClassAndTermAndExamTypeAndStatus(
                        schoolClass,
                        term,
                        examType,
                        MarksheetStatus.SUBMITTED // make sure only submitted marksheets are used
                );

        // check if all marksheets for all class subjects have been submitted
        long submittedCount = subjectSheets.size();
        long expectedCount = schoolSubjectRepository.countBySchoolClass(schoolClass);

        if (submittedCount < expectedCount) {
            throw new IncompleteMarksheetException(expectedCount - submittedCount);
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
                    : total / (double) marks.size();

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
                Comparator.comparingInt(GeneralStudentResult::getTotalMarks).reversed()
        );

        int position = 1;
        for (int i = 0; i < results.size(); i++) {
            if (i > 0 &&
                results.get(i).getTotalMarks() < results.get(i - 1).getTotalMarks()) {
                position++;
            }
            results.get(i).setPosition(position);
        }

        // Prepare class-level statistics
        int totalStudents = results.size();
        int totalSubjects = subjectSheets.size();
        int classHighestTotal = results.isEmpty() ? 0 : results.get(0).getTotalMarks();
        int classLowestTotal = results.isEmpty() ? 0 : results.get(results.size() - 1).getTotalMarks();
        double classAverageTotal = results.stream()
                .mapToDouble(GeneralStudentResult::getTotalMarks)
                .average()
                .orElse(0.0);

        // Save the compiled general marksheet
        GeneralMarksheet generalMarksheet = new GeneralMarksheet();
        generalMarksheet.setSchoolClass(schoolClass);
        generalMarksheet.setAcademicYear(subjectSheets.get(0).getAcademicYear()); // Fix: Set non-nullable AcademicYear
        generalMarksheet.setTerm(term);
        generalMarksheet.setExamType(examType);
        generalMarksheet.setGeneratedAt(LocalDateTime.now());
        
        // Populate class performance stats
        generalMarksheet.setTotalStudents(totalStudents);
        generalMarksheet.setTotalSubjects(totalSubjects);
        generalMarksheet.setClassHighestTotal(classHighestTotal);
        generalMarksheet.setClassLowestTotal(classLowestTotal);
        generalMarksheet.setClassAverageTotal(classAverageTotal);

        results.forEach(gsr -> gsr.setGeneralMarksheet(generalMarksheet));
        generalMarksheet.setResults(results);
        generalMarksheetRepository.save(generalMarksheet);
    }

    /**
     * Fetches summaries of compiled general marksheets for landing UI pages.
     */
    public List<GeneralMarksheetSummaryDTO> getLandingRows() {
        return generalMarksheetRepository.fetchLandingRows();
    }

    /**
     * Compiles aggregate dashboard metrics for compiled general marksheets.
     */
    public GeneralMarksheetDashboardDTO getDashboardStats() {
        return generalMarksheetRepository.fetchDashboardStats();
    }
}
