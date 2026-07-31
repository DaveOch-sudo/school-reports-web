package org.andali.schoolreportsweb.generalmarksheet.repositories;

import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet;
import org.andali.schoolreportsweb.generalmarksheet.dto.GeneralMarksheetDashboardDTO;
import org.andali.schoolreportsweb.generalmarksheet.dto.GeneralMarksheetSummaryDTO;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.year.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link GeneralMarksheet} persistence and querying.
 *
 * <p>The unique constraint on {@code GeneralMarksheet} covers
 * {@code (school_class_id, term, examType, academic_year_id)}, so all
 * duplicate-guard queries must include {@code academicYear}.</p>
 */
@Repository
public interface GeneralMarksheetRepository extends JpaRepository<GeneralMarksheet, Long> {

    // ── Existence / duplicate guard ──────────────────────────────────────────

    /**
     * Returns {@code true} if a GeneralMarksheet already exists for the exact
     * combination of class, term, exam type, and academic year.
     *
     * <p>This mirrors the database unique constraint and must be called before
     * attempting to compile a new GeneralMarksheet.</p>
     */
    boolean existsBySchoolClassAndTermAndExamTypeAndAcademicYear(
            SchoolClass schoolClass,
            Term term,
            ExamType examType,
            AcademicYear academicYear);

    // ── Filtered lookups ─────────────────────────────────────────────────────

    /**
     * Returns all compiled general marksheets for a given class,
     * ordered newest-first.
     */
    List<GeneralMarksheet> findBySchoolClassOrderByGeneratedAtDesc(SchoolClass schoolClass);

    // ── Projection queries ───────────────────────────────────────────────────

    /**
     * Fetches lightweight summary rows for landing/listing pages.
     *
     * <p>Uses stored fields ({@code totalStudents}, {@code totalSubjects},
     * {@code classAverageTotal}) to avoid loading full result collections.</p>
     */
    @Query("""
    SELECT new org.andali.schoolreportsweb.generalmarksheet.dto.GeneralMarksheetSummaryDTO(
        g.id,
        g.schoolClass.name,
        g.term,
        g.examType,
        g.totalStudents,
        g.totalSubjects,
        g.classAverageTotal,
        g.generatedAt
    )
    FROM GeneralMarksheet g
    ORDER BY g.generatedAt DESC
    """)
    List<GeneralMarksheetSummaryDTO> fetchLandingRows();

    /**
     * Fetches aggregate dashboard metrics:
     * total marksheets, distinct classes covered, distinct exam types, and last generation time.
     */
    @Query("""
    SELECT new org.andali.schoolreportsweb.generalmarksheet.dto.GeneralMarksheetDashboardDTO(
        COUNT(g),
        COUNT(DISTINCT g.schoolClass.id),
        COUNT(DISTINCT g.examType),
        MAX(g.generatedAt)
    )
    FROM GeneralMarksheet g
    """)
    GeneralMarksheetDashboardDTO fetchDashboardStats();
}
