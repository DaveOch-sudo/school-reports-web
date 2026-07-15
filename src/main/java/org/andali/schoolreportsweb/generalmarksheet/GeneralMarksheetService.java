package org.andali.schoolreportsweb.generalmarksheet;

import jakarta.transaction.Transactional;
import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.MarksheetStatus;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.generalmarksheet.dto.GeneralMarksheetDashboardDTO;
import org.andali.schoolreportsweb.generalmarksheet.dto.GeneralMarksheetSummaryDTO;
import org.andali.schoolreportsweb.generalmarksheet.repositories.GeneralMarksheetRepository;
import org.andali.schoolreportsweb.generalmarksheet.repositories.GeneralStudentResultRepository;
import org.andali.schoolreportsweb.generalmarksheet.repositories.SubjectResultRepository;
import org.andali.schoolreportsweb.marksheet.exception.IncompleteMarksheetException;
import org.andali.schoolreportsweb.marksheet.Marksheet;
import org.andali.schoolreportsweb.marksheet.MarksheetRepository;
import org.andali.schoolreportsweb.marksheet.StudentMark;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.student.Student;
import org.andali.schoolreportsweb.year.AcademicYear;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for compiling individual subject marksheets into a
 * {@link GeneralMarksheet} — a class-wide snapshot of every student's performance
 * across all subjects for a given term, exam type, and academic year.
 *
 * <p>The compilation is intentionally a one-way snapshot: once generated, the
 * {@link GeneralMarksheet} is independent of future edits to the source marksheets.
 * This ensures report cards remain stable after release.</p>
 */
@Service
public class GeneralMarksheetService {

    private final GeneralMarksheetRepository generalMarksheetRepository;
    private final MarksheetRepository marksheetRepository;

    public GeneralMarksheetService(GeneralMarksheetRepository generalMarksheetRepository,
                                   GeneralStudentResultRepository generalStudentResultRepository,
                                   SubjectResultRepository subjectResultRepository,
                                   MarksheetRepository marksheetRepository) {
        this.generalMarksheetRepository = generalMarksheetRepository;
        this.marksheetRepository = marksheetRepository;
    }

    // ── Compilation ──────────────────────────────────────────────────────────

    /**
     * Compiles and persists a {@link GeneralMarksheet} for the given class, term,
     * exam type, and academic year.
     *
     * <h3>Preconditions</h3>
     * <ul>
     *   <li>No general marksheet already exists for the same
     *       (schoolClass, term, examType, academicYear) combination.</li>
     *   <li>Every subject marksheet for the class/term/examType/year must be in
     *       {@link MarksheetStatus#GRADED} status. Partially-graded sets are
     *       rejected with {@link IncompleteMarksheetException}.</li>
     * </ul>
     *
     * <h3>What this method does</h3>
     * <ol>
     *   <li>Fetches all {@code GRADED} marksheets for the combination.</li>
     *   <li>Verifies completeness: graded count must equal total subject marksheets
     *       for that class/term/examType (i.e. every subject has been graded).</li>
     *   <li>Groups {@link StudentMark}s by student and builds
     *       {@link GeneralStudentResult} objects with per-subject
     *       {@link SubjectResult} snapshots.</li>
     *   <li>Sorts results by total marks (descending) and assigns positions
     *       with tie-aware ranking.</li>
     *   <li>Computes class-level statistics (highest, lowest, average total).</li>
     *   <li>Persists everything via {@link GeneralMarksheetRepository#save}.</li>
     * </ol>
     *
     * @param schoolClass  the class to compile for
     * @param term         the term (TERM_1, TERM_2, TERM_3)
     * @param examType     the exam type (BOT, MID, EOT, TEST)
     * @param academicYear the academic year scope
     * @throws IllegalStateException        if a GeneralMarksheet already exists for this combination
     * @throws IncompleteMarksheetException if any subject marksheet is not yet GRADED
     */
    @Transactional
    public GeneralMarksheet createGeneralMarksheet(SchoolClass schoolClass,
                                                   Term term,
                                                   ExamType examType,
                                                   AcademicYear academicYear) {

        // ── Step 4: Duplicate check now includes academicYear ────────────────
        // The unique constraint on GeneralMarksheet covers (school_class_id, term, examType,
        // academic_year_id), so our guard must match that exact scope.
        if (generalMarksheetRepository.existsBySchoolClassAndTermAndExamTypeAndAcademicYear(
                schoolClass, term, examType, academicYear)) {
            throw new IllegalStateException(
                    "A general marksheet already exists for class '%s', %s %s in academic year '%s'."
                            .formatted(schoolClass.getName(), term, examType, academicYear.getLabel()));
        }

        // ── Step 2a: Fetch only GRADED marksheets ────────────────────────────
        // We deliberately require GRADED (not SUBMITTED) because grades must be
        // resolved and persisted on StudentMark before compilation can copy them
        // into SubjectResult snapshots.
        List<Marksheet> gradedSheets = marksheetRepository
                .findBySchoolClassAndTermAndExamTypeAndStatusAndAcademicYear(
                        schoolClass, term, examType, MarksheetStatus.GRADED, academicYear);

        // ── Step 2b: Completeness check ──────────────────────────────────────
        // Count ALL marksheets (any status) for this combination to determine
        // how many subjects are expected. If fewer are GRADED, compilation is blocked.
        long totalSheets = marksheetRepository
                .countBySchoolClassAndTermAndExamTypeAndAcademicYear(
                        schoolClass, term, examType, academicYear);

        long gradedCount = gradedSheets.size();

        if (gradedCount < totalSheets) {
            long missing = totalSheets - gradedCount;
            throw new IncompleteMarksheetException(missing);
        }

        if (gradedSheets.isEmpty()) {
            throw new IllegalStateException(
                    "No marksheets found for class '%s', %s %s in academic year '%s'. "
                            .formatted(schoolClass.getName(), term, examType, academicYear.getLabel())
                            + "Create and grade subject marksheets before compiling.");
        }

        // ── Group all StudentMarks by student across every subject ────────────
        Map<Student, List<StudentMark>> marksByStudent =
                gradedSheets.stream()
                        .flatMap(ms -> ms.getStudentMarks().stream())
                        .collect(Collectors.groupingBy(StudentMark::getStudent));

        // ── Build GeneralStudentResult for each student ───────────────────────
        List<GeneralStudentResult> results = new ArrayList<>();

        for (Map.Entry<Student, List<StudentMark>> entry : marksByStudent.entrySet()) {
            Student student = entry.getKey();
            List<StudentMark> marks = entry.getValue();

            int total = marks.stream()
                    .mapToInt(StudentMark::getScore)
                    .sum();

            double average = marks.isEmpty() ? 0.0 : total / (double) marks.size();

            GeneralStudentResult gsr = new GeneralStudentResult();
            gsr.setStudent(student);
            gsr.setTotalMarks(total);
            gsr.setAverageMarks(average);

            // ── Step 3: Populate SubjectResult snapshots ──────────────────────
            // We snapshot subjectName at compilation time so the result remains
            // readable even if the subject is later renamed or deleted.
            // grade and remark are read from StudentMark where they were persisted
            // by resolveAllGrades() — no re-resolution needed here.
            List<SubjectResult> subjectResults = marks.stream().map(sm -> {
                SubjectResult sr = new SubjectResult();

                // Snapshot the subject name — this is the key data-integrity field
                sr.setSubjectName(sm.getMarksheet().getSchoolSubject().getName());

                // Soft reference: nullable so the snapshot survives subject deletion
                sr.setSchoolSubject(sm.getMarksheet().getSchoolSubject());

                sr.setScore(sm.getScore());

                // grade/remark were persisted as real columns in Step 1 — just copy them
                sr.setGrade(sm.getGrade());
                sr.setRemark(sm.getRemark());

                return sr;
            }).toList();

            gsr.setSubjectResults(subjectResults);
            results.add(gsr);
        }

        // ── Rank students by total marks (descending) with tie-aware positions ──
        // Tie-aware: students with the same total share a position; the next
        // distinct total gets the position after theirs (e.g. 1, 1, 3, 4...).
        results.sort(Comparator.comparingInt(GeneralStudentResult::getTotalMarks).reversed());

        int position = 1;
        for (int i = 0; i < results.size(); i++) {
            if (i > 0 && results.get(i).getTotalMarks() < results.get(i - 1).getTotalMarks()) {
                position = i + 1; // jump to actual rank, not just increment
            }
            results.get(i).setPosition(position);
        }

        // ── Compute class-level performance statistics ────────────────────────
        int totalStudents  = results.size();
        int totalSubjects  = gradedSheets.size();
        int classHighest   = results.isEmpty() ? 0 : results.get(0).getTotalMarks();
        int classLowest    = results.isEmpty() ? 0 : results.get(results.size() - 1).getTotalMarks();
        double classAverage = results.stream()
                .mapToDouble(GeneralStudentResult::getTotalMarks)
                .average()
                .orElse(0.0);

        // ── Assemble and persist the GeneralMarksheet ─────────────────────────
        GeneralMarksheet generalMarksheet = new GeneralMarksheet();
        generalMarksheet.setSchoolClass(schoolClass);
        generalMarksheet.setAcademicYear(academicYear);
        generalMarksheet.setTerm(term);
        generalMarksheet.setExamType(examType);
        generalMarksheet.setGeneratedAt(LocalDateTime.now());
        generalMarksheet.setTotalStudents(totalStudents);
        generalMarksheet.setTotalSubjects(totalSubjects);
        generalMarksheet.setClassHighestTotal(classHighest);
        generalMarksheet.setClassLowestTotal(classLowest);
        generalMarksheet.setClassAverageTotal(classAverage);

        // Wire back-references before save so cascade persists child records correctly
        results.forEach(gsr -> gsr.setGeneralMarksheet(generalMarksheet));
        generalMarksheet.setResults(results);

        return generalMarksheetRepository.save(generalMarksheet);
    }

    // ── Query methods ────────────────────────────────────────────────────────

    /**
     * Returns a compiled {@link GeneralMarksheet} by its ID, including all
     * student results and subject snapshots.
     *
     * @param id the GeneralMarksheet ID
     * @return the entity, or {@code null} if not found
     */
    public GeneralMarksheet getById(Long id) {
        return generalMarksheetRepository.findById(id).orElse(null);
    }

    /**
     * Returns all compiled general marksheets for the given class.
     *
     * @param schoolClass the class to query
     * @return list of GeneralMarksheets, most recent first
     */
    public List<GeneralMarksheet> getBySchoolClass(SchoolClass schoolClass) {
        return generalMarksheetRepository.findBySchoolClassOrderByGeneratedAtDesc(schoolClass);
    }

    /**
     * Returns lightweight summary rows suitable for listing/landing UI pages.
     * Each row contains class name, term, exam type, student count, and generation time.
     */
    public List<GeneralMarksheetSummaryDTO> getLandingRows() {
        return generalMarksheetRepository.fetchLandingRows();
    }

    /**
     * Returns aggregate dashboard metrics: total marksheets compiled,
     * distinct classes and exam types covered, and the last generation timestamp.
     */
    public GeneralMarksheetDashboardDTO getDashboardStats() {
        return generalMarksheetRepository.fetchDashboardStats();
    }
}
