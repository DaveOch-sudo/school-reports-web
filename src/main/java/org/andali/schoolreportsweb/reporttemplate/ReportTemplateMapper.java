package org.andali.schoolreportsweb.reporttemplate;

import org.springframework.stereotype.Component;

@Component
public class ReportTemplateMapper {

    public ReportTemplateDto toDto(ReportTemplate t) {
        return ReportTemplateDto.builder()
                .id(t.getId())
                .schoolId(t.getSchool().getId())
                .name(t.getName())
                .active(t.isActive())
                .logoUrl(t.getLogoUrl())
                .schoolNameOverride(t.getSchoolNameOverride())
                .showPosition(t.isShowPosition())
                .showAverage(t.isShowAverage())
                .showGrade(t.isShowGrade())
                .showRemark(t.isShowRemark())
                .showClassTeacherComment(t.isShowClassTeacherComment())
                .showHeadteacherComment(t.isShowHeadteacherComment())
                .footerText(t.getFooterText())
                .headerColor(t.getHeaderColor())
                .includedExams(t.getIncludedExams())
                .build();
    }
}
