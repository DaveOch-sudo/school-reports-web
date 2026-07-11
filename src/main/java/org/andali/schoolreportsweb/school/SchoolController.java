package org.andali.schoolreportsweb.school;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.SchoolService;
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
    public ResponseEntity<List<SchoolRequestDto>> getAll() {
        return ResponseEntity.ok(schoolService.getAll().stream().map(schoolMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolRequestDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolMapper.toDto(schoolService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SchoolRequestDto> create(@RequestBody SchoolRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolMapper.toDto(schoolService.create(schoolMapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolRequestDto> update(@PathVariable Long id, @RequestBody SchoolRequestDto dto) {
        var school = schoolService.getById(id);
        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setPhone(dto.getPhone());
        school.setEmail(dto.getEmail());
//        school.setEmis_code(dto.getEmisCode());
//        school.setMotto(dto.getMotto());
//        school.setLogoUrl(dto.getLogoUrl());
//        school.setRegistrationNumber(dto.getRegistrationNumber());
        return ResponseEntity.ok(schoolMapper.toDto(schoolService.update(school)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        schoolService.delete(id);
    }
}
