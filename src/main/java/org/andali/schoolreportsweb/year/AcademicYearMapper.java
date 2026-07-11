package org.andali.schoolreportsweb.year;

import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

    public AcademicYearDto toDto(AcademicYear y) {
        return AcademicYearDto.builder()
                .id(y.getId())
                .schoolId(y.getSchool().getId())
                .label(y.getLabel())
                .isCurrent(y.isCurrent())
                .startDate(y.getStartDate())
                .endDate(y.getEndDate())
                .build();
    }
}
