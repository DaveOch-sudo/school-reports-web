package org.andali.schoolreportsweb.reporttemplate;

import org.andali.schoolreportsweb.school.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {
    List<ReportTemplate> findAllBySchool(School school);
    Optional<ReportTemplate> findBySchoolAndActiveTrue(School school);
}
