package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.SchoolSubjectDto;
import org.andali.schoolreportsweb.model.SchoolSubject;
import org.springframework.stereotype.Component;

@Component
public class SchoolSubjectMapper {

    public SchoolSubjectDto toDto(SchoolSubject s) {
        return SchoolSubjectDto.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .schoolClassId(s.getSchoolClass().getId())
                .schoolClassName(s.getSchoolClass().getName())
                .build();
    }
}
