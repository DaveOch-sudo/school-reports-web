package org.andali.schoolreportsweb.gradingscale;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grading-scales")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GradingScaleController {

    private final GradingScaleService gradingScaleService;
    private final GradingScaleMapper gradingScaleMapper;
    private final SchoolService schoolService;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<GradingScaleDto>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(gradingScaleService.getAllBySchool(schoolService.getById(schoolId))
                .stream().map(gradingScaleMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradingScaleDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gradingScaleMapper.toDto(gradingScaleService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<GradingScaleDto> create(@RequestBody GradingScaleDto dto) {
        GradingScale scale = new GradingScale();
        scale.setName(dto.getName());
        scale.setSchool(schoolService.getById(dto.getSchoolId()));
        if (dto.getSteps() != null) {
            dto.getSteps().forEach(s -> {
                GradeStep step = new GradeStep();
                step.setGrade(s.getGrade());
                step.setMinScore(s.getMinScore());
                step.setMaxScore(s.getMaxScore());
                step.setRemark(s.getRemark());
                scale.addStep(step);
            });
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gradingScaleMapper.toDto(gradingScaleService.create(scale)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradingScaleDto> update(@PathVariable Long id, @RequestBody GradingScaleDto dto) {
        GradingScale scale = gradingScaleService.getById(id);
        scale.setName(dto.getName());
        return ResponseEntity.ok(gradingScaleMapper.toDto(gradingScaleService.create(scale)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        gradingScaleService.delete(id);
    }
}
