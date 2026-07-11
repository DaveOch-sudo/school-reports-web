package org.andali.schoolreportsweb.school;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.dto.SchoolRequestDto;
import org.andali.schoolreportsweb.school.dto.SchoolResponseDTO;
import org.andali.schoolreportsweb.school.dto.SchoolUpdateRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public SchoolResponseDTO create(SchoolRequestDto dto) {
        School school = SchoolMapper.toEntity(dto);
        return SchoolMapper.toResponseDto(schoolRepository.save(school));
    }

    public SchoolResponseDTO getById(Long id) {
        return schoolRepository.findById(id)
                .map(SchoolMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + id));
    }

    public SchoolResponseDTO getByName(String name) {
        return schoolRepository.findByName(name)
                .map(SchoolMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + name));
    }

    public List<SchoolResponseDTO> getAll() {
        return schoolRepository.findAll().stream()
                .map(SchoolMapper::toResponseDto)
                .toList();
    }

    public SchoolResponseDTO update(SchoolUpdateRequestDto dto) {
        School school = SchoolMapper.toEntity(dto);
        schoolRepository.save(school);
        return SchoolMapper.toResponseDto(school);
    }

    public void delete(Long id) {
        schoolRepository.deleteById(id);
    }
}
