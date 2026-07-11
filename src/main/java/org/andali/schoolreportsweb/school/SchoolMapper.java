package org.andali.schoolreportsweb.school;

import org.andali.schoolreportsweb.school.dto.SchoolRequestDto;
import org.andali.schoolreportsweb.school.dto.SchoolResponseDTO;
import org.andali.schoolreportsweb.school.dto.SchoolUpdateRequestDto;
import org.springframework.stereotype.Component;

@Component
public class SchoolMapper {

    public static SchoolRequestDto toDto(School school) {
        return SchoolRequestDto.builder()
                .name(school.getName())
                .address(school.getAddress())
                .phone(school.getPhone())
                .email(school.getEmail())
                .build();
    }

    public static School toEntity(SchoolRequestDto dto) {
        School school = new School();
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        return school;
    }

    public static School toEntity(SchoolUpdateRequestDto dto) {
        School school = new School();
        school.setId(dto.getId());
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        school.setEmis_code(dto.getEmis_code());
        school.setLogoUrl(dto.getLogoUrl());
        school.setMotto(dto.getMotto());
        return school;
    }

    public static SchoolResponseDTO toResponseDto(School school) {
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
