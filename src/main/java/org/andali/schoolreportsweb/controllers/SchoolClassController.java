package org.andali.schoolreportsweb.controllers;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.dto.SchoolClassDto;
import org.andali.schoolreportsweb.mapper.SchoolClassMapper;
import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.service.GradingScaleService;
import org.andali.schoolreportsweb.service.SchoolClassService;
import org.andali.schoolreportsweb.service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService schoolClassService;
    private final SchoolClassMapper schoolClassMapper;
    private final SchoolService schoolService;
    private final GradingScaleService gradingScaleService;

    @GetMapping
    public ResponseEntity<List<SchoolClassDto>> getAll() {
        return ResponseEntity.ok(schoolClassService.getAllSchoolClasses().stream()
                .map(schoolClassMapper::toDto).toList());
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<SchoolClassDto>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolClassService.getAllBySchool(schoolService.getById(schoolId))
                .stream().map(schoolClassMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolClassMapper.toDto(schoolClassService.getSchoolClassById(id)));
    }

    @PostMapping
    public ResponseEntity<SchoolClassDto> create(@RequestBody SchoolClassDto dto) {
        SchoolClass sc = new SchoolClass();
        sc.setName(dto.getName());
        sc.setSchool(schoolService.getById(dto.getSchoolId()));
        if (dto.getDefaultGradingScaleId() != null)
            sc.setDefaultGradingScale(gradingScaleService.getById(dto.getDefaultGradingScaleId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolClassMapper.toDto(schoolClassService.save(sc)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolClassDto> update(@PathVariable Long id, @RequestBody SchoolClassDto dto) {
        SchoolClass sc = schoolClassService.getSchoolClassById(id);
        sc.setName(dto.getName());
        if (dto.getDefaultGradingScaleId() != null)
            sc.setDefaultGradingScale(gradingScaleService.getById(dto.getDefaultGradingScaleId()));
        return ResponseEntity.ok(schoolClassMapper.toDto(schoolClassService.save(sc)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        schoolClassService.deleteById(id);
    }
}
