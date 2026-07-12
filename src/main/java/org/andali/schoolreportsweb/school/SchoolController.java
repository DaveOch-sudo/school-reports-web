package org.andali.schoolreportsweb.school;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.dto.SchoolRequestDto;
import org.andali.schoolreportsweb.school.SchoolMapper;
import org.andali.schoolreportsweb.school.dto.SchoolResponseDto;
import org.andali.schoolreportsweb.school.dto.SchoolUpdateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;
   
    @GetMapping
    public ResponseEntity<List<SchoolResponseDto>> getAll() {
        return ResponseEntity.ok(schoolService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schoolService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SchoolResponseDto> create(@RequestBody SchoolRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schoolService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolResponseDto> update(@PathVariable Long id,
                                                    @Validated @RequestBody SchoolUpdateRequestDto dto) {
        var school = schoolService.getById(id);
        if (school == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schoolService.update( id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        schoolService.delete(id);
    }
}
