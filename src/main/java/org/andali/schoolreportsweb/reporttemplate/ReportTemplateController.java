package org.andali.schoolreportsweb.reporttemplate;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report-templates")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;
    private final ReportTemplateMapper reportTemplateMapper;
    private final SchoolService schoolService;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<ReportTemplateDto>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(reportTemplateService.getAllBySchool(schoolService.getSchoolById(schoolId))
                .stream().map(reportTemplateMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportTemplateDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportTemplateMapper.toDto(reportTemplateService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ReportTemplateDto> create(@RequestBody ReportTemplateDto dto) {
        ReportTemplate t = new ReportTemplate();
        t.setSchool(schoolService.getSchoolById(dto.getSchoolId()));
        t.setName(dto.getName());
        t.setLogoUrl(dto.getLogoUrl());
        t.setSchoolNameOverride(dto.getSchoolNameOverride());
        t.setShowPosition(dto.isShowPosition());
        t.setShowAverage(dto.isShowAverage());
        t.setShowGrade(dto.isShowGrade());
        t.setShowRemark(dto.isShowRemark());
        t.setShowClassTeacherComment(dto.isShowClassTeacherComment());
        t.setShowHeadteacherComment(dto.isShowHeadteacherComment());
        t.setFooterText(dto.getFooterText());
        t.setHeaderColor(dto.getHeaderColor());
        if (dto.getIncludedExams() != null) t.setIncludedExams(dto.getIncludedExams());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportTemplateMapper.toDto(reportTemplateService.create(t)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportTemplateDto> update(@PathVariable Long id, @RequestBody ReportTemplateDto dto) {
        ReportTemplate t = reportTemplateService.getById(id);
        t.setName(dto.getName());
        t.setLogoUrl(dto.getLogoUrl());
        t.setSchoolNameOverride(dto.getSchoolNameOverride());
        t.setShowPosition(dto.isShowPosition());
        t.setShowAverage(dto.isShowAverage());
        t.setShowGrade(dto.isShowGrade());
        t.setShowRemark(dto.isShowRemark());
        t.setShowClassTeacherComment(dto.isShowClassTeacherComment());
        t.setShowHeadteacherComment(dto.isShowHeadteacherComment());
        t.setFooterText(dto.getFooterText());
        t.setHeaderColor(dto.getHeaderColor());
        if (dto.getIncludedExams() != null) t.setIncludedExams(dto.getIncludedExams());
        return ResponseEntity.ok(reportTemplateMapper.toDto(reportTemplateService.update(t)));
    }

    @PatchMapping("/{id}/set-active")
    public ResponseEntity<ReportTemplateDto> setActive(@PathVariable Long id, @RequestParam Long schoolId) {
        return ResponseEntity.ok(reportTemplateMapper.toDto(
                reportTemplateService.setAsActive(id, schoolService.getSchoolById(schoolId))));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reportTemplateService.delete(id);
    }
}
