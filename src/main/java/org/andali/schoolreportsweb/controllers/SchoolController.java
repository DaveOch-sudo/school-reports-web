package org.andali.schoolreportsweb.controllers;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.dto.SchoolDto;
import org.andali.schoolreportsweb.mapper.SchoolMapper;
import org.andali.schoolreportsweb.service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;
    private final SchoolMapper schoolMapper;

    @GetMapping
    public ResponseEntity<List<SchoolDto>> getAll() {
        return ResponseEntity.ok(schoolService.getAll().stream().map(schoolMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolMapper.toDto(schoolService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SchoolDto> create(@RequestBody SchoolDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolMapper.toDto(schoolService.create(schoolMapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolDto> update(@PathVariable Long id, @RequestBody SchoolDto dto) {
        var school = schoolService.getById(id);
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
        school.setEmis_code(dto.getEmisCode());
        school.setMotto(dto.getMotto());
        school.setLogoUrl(dto.getLogoUrl());
        school.setRegistrationNumber(dto.getRegistrationNumber());
        return ResponseEntity.ok(schoolMapper.toDto(schoolService.update(school)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        schoolService.delete(id);
    }
}
