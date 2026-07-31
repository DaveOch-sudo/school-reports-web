package org.andali.schoolreportsweb.student;

import org.andali.schoolreportsweb.student.dto.StudentRequestDto;
import org.andali.schoolreportsweb.student.dto.StudentResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    // get all students
    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents().stream()
                .map(studentMapper::toDto)
                .toList());
    }

    // get student
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(studentMapper.toDto(studentService.getStudentById(id)));
    }

    // create student
    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(
            @RequestBody StudentResponseDto studentDto) {
        StudentResponseDto responseDto = studentMapper.toDto(studentService.createStudent(studentDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // intended for bulk import of students
    @PostMapping("/bulk")
    public ResponseEntity<List<StudentResponseDto>> importStudents(
            @RequestBody List<StudentRequestDto> studentImports) {
        studentService.addMany(studentImports);
        return ResponseEntity.status(HttpStatus.CREATED).body(null); // no response
    }

    // update student
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @PathVariable Long id,
            @Validated @RequestBody StudentResponseDto request) {
        return ResponseEntity.ok(
                studentMapper.toDto(studentService.updateStudent(id, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}
