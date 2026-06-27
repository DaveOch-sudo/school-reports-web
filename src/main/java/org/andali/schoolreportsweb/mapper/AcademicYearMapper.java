package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.AcademicYearDto;
import org.andali.schoolreportsweb.model.AcademicYear;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

    public AcademicYearDto toDto(AcademicYear y) {
        return AcademicYearDto.builder()
                .id(y.getId())
                .schoolId(y.getSchool().getId())
                .label(y.getLabel())
                .current(y.isCurrent())
                .startDate(y.getStartDate())
                .endDate(y.getEndDate())
                .build();
    }
}
