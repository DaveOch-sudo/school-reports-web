package org.andali.schoolreportsweb.school;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.dto.SchoolRequestDto;
import org.andali.schoolreportsweb.school.dto.SchoolResponseDto;
import org.andali.schoolreportsweb.school.dto.SchoolUpdateRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public SchoolResponseDto create(SchoolRequestDto dto) {
        School school = SchoolMapper.toEntity(dto);
        return SchoolMapper.toResponseDto(schoolRepository.save(school));
    }

    public SchoolResponseDto getById(Long id) {
        return schoolRepository.findById(id)
                .map(SchoolMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + id));
    }
    public School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + id));
    }

    public SchoolResponseDto getByName(String name) {
        return schoolRepository.findByName(name)
                .map(SchoolMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + name));
    }

    public List<SchoolResponseDto> getAll() {
        return schoolRepository.findAll().stream()
                .map(SchoolMapper::toResponseDto)
                .toList();
    }

    public SchoolResponseDto update(Long id, SchoolUpdateRequestDto dto) {
        School school = schoolRepository.getSchoolById(id);

        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        school.setEmisCode(dto.getEmisCode());
        school.setLogoUrl(dto.getLogoUrl());
        school.setMotto(dto.getMotto());
        school.setName(dto.getName());
        school.setRegistrationNumber(String.valueOf(dto.getRegistrationNumber()));
        school.setAddress(dto.getAddress());

        schoolRepository.save(school);
        return SchoolMapper.toResponseDto(school);
    }

    public void delete(Long id) {
        schoolRepository.deleteById(id);
    }
}
