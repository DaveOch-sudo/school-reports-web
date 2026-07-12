package org.andali.schoolreportsweb.schoolclass;

import org.andali.schoolreportsweb.schoolclass.dto.SchoolClassReguestDto;
import org.andali.schoolreportsweb.schoolclass.dto.SchoolClassResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SchoolClassMapper {

    public SchoolClassResponseDto toDto(SchoolClass sc) {
        return SchoolClassResponseDto.builder()
                .id(sc.getId())
                .name(sc.getName())
                .schoolId(sc.getSchool().getId())
                .defaultGradingScaleId(sc.getDefaultGradingScale() != null ? sc.getDefaultGradingScale().getId() : null)
                .build();
    }
}
