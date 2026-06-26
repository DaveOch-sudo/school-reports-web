package org.andali.schoolreportsweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeStepRowDTO {
    private String grade;
    private Integer minScore;
    private Integer maxScore;
    private String remark;
}