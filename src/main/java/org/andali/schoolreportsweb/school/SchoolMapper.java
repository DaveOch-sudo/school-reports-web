package org.andali.schoolreportsweb.school;

import org.andali.schoolreportsweb.school.dto.SchoolRequestDto;
import org.andali.schoolreportsweb.school.dto.SchoolResponseDto;
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

//    public static School toEntity(SchoolUpdateRequestDto dto) {
//        School school = new School();
//        school.setId(Long.parseLong(dto.getId()));
//        school.setName(dto.getName());
//        school.setAddress(dto.getAddress());
//        school.setPhone(dto.getPhone());
//        school.setEmail(dto.getEmail());
//        school.setEmisCode(dto.getEmisCode());
//        school.setLogoUrl(dto.getLogoUrl());
//        school.setMotto(dto.getMotto());
//        return school;
//    }

    public static SchoolResponseDto toResponseDto(School school) {
        return SchoolResponseDto.builder()
                .id(school.getId().toString())
                .name(school.getName())
                .address(school.getAddress())
                .phone(school.getPhone())
                .email(school.getEmail())
                .emisCode(school.getEmisCode())
                .logoUrl(school.getLogoUrl())
                .registrationNumber(school.getRegistrationNumber())
                .build();

    }

}
