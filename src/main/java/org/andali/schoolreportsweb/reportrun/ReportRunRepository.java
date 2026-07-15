package org.andali.schoolreportsweb.reportrun;

import org.andali.schoolreportsweb.enums.ReportRunStatus;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.year.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRunRepository extends JpaRepository<ReportRun, Long> {

    /** Duplicate guard — mirrors the unique constraint on (schoolClass, academicYear, term). */
    boolean existsBySchoolClassAndAcademicYearAndTerm(
            SchoolClass schoolClass, AcademicYear academicYear, Term term);

    /** All runs for a class, newest first. */
    List<ReportRun> findBySchoolClassOrderByCreatedAtDesc(SchoolClass schoolClass);

    /** All runs for a class scoped to one academic year. */
    List<ReportRun> findBySchoolClassAndAcademicYear(SchoolClass schoolClass, AcademicYear academicYear);

    /** Runs filtered by status — useful for admin dashboards. */
    List<ReportRun> findByStatus(ReportRunStatus status);

    Optional<ReportRun> findBySchoolClassAndAcademicYearAndTerm(
            SchoolClass schoolClass, AcademicYear academicYear, Term term);
}
