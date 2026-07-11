package org.andali.schoolreportsweb.subject;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.school.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SchoolSubjectController {

    private final SchoolSubjectService schoolSubjectService;
    private final SchoolSubjectMapper schoolSubjectMapper;
    private final SchoolClassService schoolClassService;
    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<SchoolSubjectDto>> getAll() {
        return ResponseEntity.ok(schoolSubjectService.getAllSubjects().stream()
                .map(schoolSubjectMapper::toDto).toList());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<SchoolSubjectDto>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(
                schoolSubjectService.getAllBySchoolClass(schoolClassService.getSchoolClassById(classId))
                        .stream().map(schoolSubjectMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolSubjectDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolSubjectMapper.toDto(schoolSubjectService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SchoolSubjectDto> create(@RequestBody SchoolSubjectDto dto) {
        SchoolSubject subject = new SchoolSubject();
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setSchoolClass(schoolClassService.getSchoolClassById(dto.getSchoolClassId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolSubjectMapper.toDto(schoolSubjectService.save(subject)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolSubjectDto> update(@PathVariable Long id, @RequestBody SchoolSubjectDto dto) {
        SchoolSubject subject = schoolSubjectService.getById(id);
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        if (dto.getSchoolClassId() != null)
            subject.setSchoolClass(schoolClassService.getSchoolClassById(dto.getSchoolClassId()));
        return ResponseEntity.ok(schoolSubjectMapper.toDto(schoolSubjectService.save(subject)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        schoolSubjectService.deleteById(id);
    }
}
