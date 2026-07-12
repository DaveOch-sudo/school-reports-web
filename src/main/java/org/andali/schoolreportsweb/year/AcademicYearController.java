package org.andali.schoolreportsweb.year;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic-years")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;
    private final AcademicYearMapper academicYearMapper;
    private final SchoolService schoolService;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<AcademicYearDto>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(academicYearService.getAllBySchool(schoolService.getSchoolById(schoolId))
                .stream().map(academicYearMapper::toDto).toList());
    }

    @GetMapping("/school/{schoolId}/current")
    public ResponseEntity<AcademicYearDto> getCurrent(@PathVariable Long schoolId) {
        return ResponseEntity.ok(academicYearMapper.toDto(
                academicYearService.getCurrentYear(schoolService.getSchoolById(schoolId))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicYearDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(academicYearMapper.toDto(academicYearService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<AcademicYearDto> create(@RequestBody AcademicYearDto dto) {
        AcademicYear year = new AcademicYear();
        year.setSchool(schoolService.getSchoolById(dto.getSchoolId()));
        year.setLabel(dto.getLabel());
        year.setCurrent(dto.isCurrent());
        year.setStartDate(dto.getStartDate());
        year.setEndDate(dto.getEndDate());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(academicYearMapper.toDto(academicYearService.create(year)));
    }

    @PatchMapping("/{id}/set-current")
    public ResponseEntity<AcademicYearDto> setCurrent(@PathVariable Long id, @RequestParam Long schoolId) {
        return ResponseEntity.ok(academicYearMapper.toDto(
                academicYearService.setAsCurrent(id, schoolService.getSchoolById(schoolId))));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        academicYearService.delete(id);
    }
}
