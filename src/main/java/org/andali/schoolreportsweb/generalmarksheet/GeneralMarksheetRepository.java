package org.andali.schoolreportsweb.generalmarksheet;

import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralMarksheetRepository extends JpaRepository<GeneralMarksheet, Long> {
    boolean existsBySchoolClassAndTermAndExamType(SchoolClass schoolClass, Term term, ExamType examType);

    @Query("""
    SELECT new org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheetSummaryDTO(
        g.id,
        g.schoolClass.name,
        g.term,
        g.examType,
        SIZE(g.results),
        SIZE(g.results),
        null,
        g.generatedAt
    )
    FROM GeneralMarksheet g
    ORDER BY g.generatedAt DESC
    """)
    List<GeneralMarksheetSummaryDTO> fetchLandingRows();

    @Query("""
    SELECT new org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheetDashboardDTO(
        COUNT(g),
        COUNT(DISTINCT g.schoolClass.id),
        COUNT(DISTINCT g.examType),
        MAX(g.generatedAt)
    )
    FROM GeneralMarksheet g
    """)
    GeneralMarksheetDashboardDTO fetchDashboardStats();
}
