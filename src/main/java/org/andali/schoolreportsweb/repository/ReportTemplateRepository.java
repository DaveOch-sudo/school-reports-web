package org.andali.schoolreportsweb.repository;

import org.andali.schoolreportsweb.model.ReportTemplate;
import org.andali.schoolreportsweb.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {
    List<ReportTemplate> findAllBySchool(School school);
    Optional<ReportTemplate> findBySchoolAndActiveTrue(School school);
}
