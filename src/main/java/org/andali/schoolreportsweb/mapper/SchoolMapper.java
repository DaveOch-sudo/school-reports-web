package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.SchoolDto;
import org.andali.schoolreportsweb.model.School;
import org.springframework.stereotype.Component;

@Component
public class SchoolMapper {

    public SchoolDto toDto(School school) {
        return SchoolDto.builder()
                .id(school.getId())
                .name(school.getName())
                .address(school.getAddress())
                .phone(school.getPhone())
                .email(school.getEmail())
                .emisCode(school.getEmis_code())
                .motto(school.getMotto())
                .logoUrl(school.getLogoUrl())
                .registrationNumber(school.getRegistrationNumber())
                .build();
    }

    public School toEntity(SchoolDto dto) {
        School school = new School();
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        school.setEmis_code(dto.getEmisCode());
        school.setMotto(dto.getMotto());
        school.setLogoUrl(dto.getLogoUrl());
        school.setRegistrationNumber(dto.getRegistrationNumber());
        return school;
    }
}
