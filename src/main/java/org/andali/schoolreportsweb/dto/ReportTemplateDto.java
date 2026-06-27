package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.model.enums.ExamType;

import java.util.Set;

@Data
@Builder
public class ReportTemplateDto {
    private Long id;
    private Long schoolId;
    private String name;
    private boolean active;
    private String logoUrl;
    private String schoolNameOverride;
    private boolean showPosition;
    private boolean showAverage;
    private boolean showGrade;
    private boolean showRemark;
    private boolean showClassTeacherComment;
    private boolean showHeadteacherComment;
    private String footerText;
    private String headerColor;
    private Set<ExamType> includedExams;
}
