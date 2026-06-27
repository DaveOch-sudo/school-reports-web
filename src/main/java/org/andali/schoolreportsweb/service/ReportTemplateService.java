package org.andali.schoolreportsweb.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.model.ReportTemplate;
import org.andali.schoolreportsweb.model.School;
import org.andali.schoolreportsweb.repository.ReportTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportTemplateService {

    private final ReportTemplateRepository reportTemplateRepository;

    public ReportTemplate create(ReportTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        return reportTemplateRepository.save(template);
    }

    public ReportTemplate getById(Long id) {
        return reportTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReportTemplate not found: " + id));
    }

    public List<ReportTemplate> getAllBySchool(School school) {
        return reportTemplateRepository.findAllBySchool(school);
    }

    public ReportTemplate getActiveTemplate(School school) {
        return reportTemplateRepository.findBySchoolAndActiveTrue(school)
                .orElseThrow(() -> new IllegalStateException("No active report template for school: " + school.getId()));
    }

    public ReportTemplate update(ReportTemplate template) {
        template.setUpdatedAt(LocalDateTime.now());
        return reportTemplateRepository.save(template);
    }

    /** Sets the given template as active and deactivates all others for that school. */
    @Transactional
    public ReportTemplate setAsActive(Long id, School school) {
        reportTemplateRepository.findAllBySchool(school)
                .forEach(t -> { t.setActive(false); reportTemplateRepository.save(t); });
        ReportTemplate template = getById(id);
        template.setActive(true);
        return reportTemplateRepository.save(template);
    }

    public void delete(Long id) {
        reportTemplateRepository.deleteById(id);
    }
}
