package org.andali.schoolreportsweb.subject;

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
