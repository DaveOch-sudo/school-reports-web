package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GradingScaleDto {
    private Long id;
    private String name;
    private Long schoolId;
    private List<GradeStepDto> steps;

    @Data
    @Builder
    public static class GradeStepDto {
        private Long id;
        private String grade;
        private Integer minScore;
        private Integer maxScore;
        private String remark;
    }
}
