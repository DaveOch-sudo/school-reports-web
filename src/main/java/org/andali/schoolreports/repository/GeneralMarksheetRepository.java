package org.andali.schoolreports.repository;

import org.andali.schoolreports.dto.GeneralMarksheetDashboardDTO;
import org.andali.schoolreports.dto.GeneralMarksheetSummaryDTO;
import org.andali.schoolreports.model.GeneralMarksheet;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralMarksheetRepository extends JpaRepository<GeneralMarksheet, Long> {
    boolean existsBySchoolClassAndTermAndExamType(SchoolClass schoolClass, Term term, ExamType examType);

    @Query("""
    SELECT new org.andali.schoolreports.dto.GeneralMarksheetSummaryDTO(
        g.id,
        g.schoolClass.className,
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
    SELECT new org.andali.schoolreports.dto.GeneralMarksheetDashboardDTO(
        COUNT(g),
        COUNT(DISTINCT g.schoolClass.id),
        COUNT(DISTINCT g.examType),
        MAX(g.generatedAt)
    )
    FROM GeneralMarksheet g
    """)
    GeneralMarksheetDashboardDTO fetchDashboardStats();
}
