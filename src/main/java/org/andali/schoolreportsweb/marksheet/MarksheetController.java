package org.andali.schoolreportsweb.marksheet;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.enums.MarksheetStatus;
import org.andali.schoolreportsweb.gradingscale.GradingScaleService;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.student.StudentService;
import org.andali.schoolreportsweb.subject.SchoolSubjectService;
import org.andali.schoolreportsweb.year.AcademicYearService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/marksheets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MarksheetController {

    private final MarksheetService marksheetService;
    private final SchoolClassService schoolClassService;
    private final SchoolSubjectService schoolSubjectService;
    private final AcademicYearService academicYearService;
    private final GradingScaleService gradingScaleService;
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<MarksheetDto>> getAll() {
        return ResponseEntity.ok(marksheetService.getAllMarksheets().stream()
                .map(MarksheetMapper::toDto).toList());
    }
    
    @GetMapping("/school/{id}")
    public ResponseEntity<List<MarksheetDto>> getAllBySchoolId(@PathVariable Long schoolId) {
        return ResponseEntity.ok(marksheetService.getMarksheetsBySchoolId(schoolId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<MarksheetDto>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(marksheetService.getMarksheetsByClassId(classId)
                .stream().map(MarksheetMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarksheetDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(MarksheetMapper.toDto(marksheetService.getMarksheetById(id)));
    }

    @PostMapping
    public ResponseEntity<MarksheetDto> create(@RequestBody MarksheetDto dto) {
        marksheetService.addNewMarksheet(MarksheetMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    /** Replace all student marks on a marksheet */
    @PutMapping("/{id}/marks")
    public ResponseEntity<MarksheetDto> updateMarks(@PathVariable Long id,
            @RequestBody List<MarksheetDto.StudentMarkDto> marks) {
        Marksheet m = marksheetService.getMarksheetById(id);
        m.getStudentMarks().clear();
        marks.forEach(dto -> {
            StudentMark sm = new StudentMark();
            sm.setStudent(studentService.getStudentById(dto.getStudentId()));
            sm.setScore(dto.getScore());
            m.addStudentMark(sm);
        });
        m.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(MarksheetMapper.toDto(marksheetService.updateMarksheet(m)));
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<MarksheetDto> submit(@PathVariable Long id) {
        Marksheet m = marksheetService.getMarksheetById(id);
        m.setStatus(MarksheetStatus.SUBMITTED);
        return ResponseEntity.ok(MarksheetMapper.toDto(marksheetService.updateMarksheet(m)));
    }

    @PatchMapping("/{id}/grade")
    public ResponseEntity<MarksheetDto> grade(@PathVariable Long id) {
        Marksheet m = marksheetService.getMarksheetById(id);
        marksheetService.resolveAllGrades(m);
        m.setStatus(MarksheetStatus.GRADED);
        return ResponseEntity.ok(MarksheetMapper.toDto(marksheetService.updateMarksheet(m)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        marksheetService.deleteMarksheetById(id);
    }
}
