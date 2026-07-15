package org.andali.schoolreportsweb.reportrun;

import org.andali.schoolreportsweb.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportCardRepository extends JpaRepository<ReportCard, Long> {

    /** All cards belonging to a run — used for bulk comment updates and PDF generation. */
    List<ReportCard> findByReportRun(ReportRun reportRun);

    /** Lookup a specific student's card within a run. */
    Optional<ReportCard> findByReportRunAndStudent(ReportRun reportRun, Student student);

    /** All cards ever generated for a student across all runs. */
    List<ReportCard> findByStudent(Student student);

    boolean existsByReportRunAndStudent(ReportRun reportRun, Student student);
}
