package org.andali.schoolreportsweb.schoolclass;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.gradingscale.GradingScaleService;
import org.andali.schoolreportsweb.school.SchoolService;
import org.andali.schoolreportsweb.schoolclass.dto.SchoolClassReguestDto;
import org.andali.schoolreportsweb.schoolclass.dto.SchoolClassResponseDto;
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
    public ResponseEntity<List<SchoolClassResponseDto>> getAll() {
        return ResponseEntity.ok(schoolClassService.getAllSchoolClasses().stream()
                .map(schoolClassMapper::toDto).toList());
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<SchoolClassResponseDto>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolClassService.getAllBySchool(schoolService.getSchoolById(schoolId))
                .stream().map(schoolClassMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolClassMapper.toDto(schoolClassService.getSchoolClassById(id)));
    }

    @PostMapping
    public ResponseEntity<SchoolClassResponseDto> create(@RequestBody SchoolClassReguestDto dto) {
        SchoolClass sc = new SchoolClass();
        sc.setName(dto.getName());
        sc.setSchool(schoolService.getSchoolById(dto.getSchoolId()));
        if (dto.getDefaultGradingScaleId() != null)
            sc.setDefaultGradingScale(gradingScaleService.getById(dto.getDefaultGradingScaleId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolClassMapper.toDto(schoolClassService.save(sc)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolClassResponseDto> update(@PathVariable Long id, @RequestBody SchoolClassReguestDto dto) {
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
