package org.andali.schoolreportsweb.school;

import org.springframework.stereotype.Component;

@Component
public class SchoolMapper {

    public SchoolRequestDto toDto(School school) {
        return SchoolRequestDto.builder()
                .name(school.getName())
                .address(school.getAddress())
                .phone(school.getPhone())
                .email(school.getEmail())
                .build();
    }

    public School toEntity(SchoolRequestDto dto) {
        School school = new School();
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        return school;
    }

    public SchoolResponseDTO toResponseDto(School school) {
        return SchoolResponseDTO.builder()
                .id(school.getId().toString())
                .name(school.getName())
                .address(school.getAddress())
                .phone(school.getPhone())
                .email(school.getEmail())
                .emisCode(school.getEmis_code())
                .logoUrl(school.getLogoUrl())
                .registrationNumber(school.getRegistrationNumber())
                .build();

    }
}
