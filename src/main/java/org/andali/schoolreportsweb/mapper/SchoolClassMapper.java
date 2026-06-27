package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.SchoolClassDto;
import org.andali.schoolreportsweb.model.SchoolClass;
import org.springframework.stereotype.Component;

@Component
public class SchoolClassMapper {

    public SchoolClassDto toDto(SchoolClass sc) {
        return SchoolClassDto.builder()
                .id(sc.getId())
                .name(sc.getName())
                .schoolId(sc.getSchool().getId())
                .schoolName(sc.getSchool().getName())
                .defaultGradingScaleId(sc.getDefaultGradingScale() != null ? sc.getDefaultGradingScale().getId() : null)
                .defaultGradingScaleName(sc.getDefaultGradingScale() != null ? sc.getDefaultGradingScale().getName() : null)
                .build();
    }
}
