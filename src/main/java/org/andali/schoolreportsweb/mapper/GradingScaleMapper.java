package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.GradingScaleDto;
import org.andali.schoolreportsweb.model.GradingScale;
import org.springframework.stereotype.Component;

@Component
public class GradingScaleMapper {

    public GradingScaleDto toDto(GradingScale gs) {
        return GradingScaleDto.builder()
                .id(gs.getId())
                .name(gs.getName())
                .schoolId(gs.getSchool().getId())
                .steps(gs.getSteps().stream().map(step ->
                        GradingScaleDto.GradeStepDto.builder()
                                .id(step.getId())
                                .grade(step.getGrade())
                                .minScore(step.getMinScore())
                                .maxScore(step.getMaxScore())
                                .remark(step.getRemark())
                                .build()
                ).toList())
                .build();
    }
}
