package org.andali.schoolreportsweb.subject;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.school.SchoolService;
import org.andali.schoolreportsweb.subject.dto.SubjectRequestDto;
import org.andali.schoolreportsweb.subject.dto.SubjectResponseDto;
import org.andali.schoolreportsweb.subject.dto.SubjectUpdateDto;
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
    private final SchoolClassService schoolClassService;
    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAll() {
        return ResponseEntity.ok(schoolSubjectService.getAllSubjects());

    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<SubjectResponseDto>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(
                schoolSubjectService.getAllBySchoolClass(schoolClassService.getSchoolClassById(classId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolSubjectService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SubjectResponseDto> create(@RequestBody SubjectRequestDto dto) {
         return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolSubjectService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> update(@PathVariable Long id, @RequestBody SubjectUpdateDto dto) {
        SubjectResponseDto subject = schoolSubjectService.getById(id);
        if (subject != null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schoolSubjectService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        schoolSubjectService.deleteById(id);
    }
}
