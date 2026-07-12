package org.andali.schoolreportsweb.subject;

import org.andali.schoolreportsweb.schoolclass.SchoolClassRepository;
import org.andali.schoolreportsweb.subject.dto.SubjectRequestDto;
import org.andali.schoolreportsweb.subject.dto.SubjectResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SchoolSubjectMapper {
    private static SchoolClassRepository schoolClassRepository;

    public SchoolSubjectMapper(SchoolClassRepository schoolClassRepository) {
        SchoolSubjectMapper.schoolClassRepository = schoolClassRepository;
    }

    public static SubjectResponseDto toDto(SchoolSubject s) {
        return SubjectResponseDto.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .schoolClassId(s.getSchoolClass().getId())
                .schoolClassName(s.getSchoolClass().getName())
                .build();
    }

    public static SchoolSubject toEntity(SubjectRequestDto dto) {
        SchoolSubject subject = new SchoolSubject();
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setSchoolClass(schoolClassRepository.findById(dto.getSchoolClassId()).get());
        return subject;
    }
}
