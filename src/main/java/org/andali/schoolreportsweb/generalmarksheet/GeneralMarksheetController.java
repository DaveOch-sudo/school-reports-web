package org.andali.schoolreportsweb.generalmarksheet;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.generalmarksheet.dto.*;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.year.AcademicYear;
import org.andali.schoolreportsweb.year.AcademicYearService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for {@link GeneralMarksheet} operations.
 *
 * <p>Base path: {@code /api/v1/general-marksheets}</p>
 *
 * <h3>Endpoints</h3>
 * <ul>
 *   <li>{@code POST   /}              — Compile a new GeneralMarksheet</li>
 *   <li>{@code GET    /{id}}          — Fetch a compiled marksheet by ID</li>
 *   <li>{@code GET    /class/{id}}    — List all compiled marksheets for a class</li>
 *   <li>{@code GET    /landing}       — Summary rows for listing UI</li>
 *   <li>{@code GET    /dashboard}     — Aggregate dashboard statistics</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/general-marksheets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GeneralMarksheetController {

    private final GeneralMarksheetService generalMarksheetService;
    private final SchoolClassService schoolClassService;
    private final AcademicYearService academicYearService;
    private final GeneralMarksheetMapper generalMarksheetMapper;

    // ── Compilation ──────────────────────────────────────────────────────────

    /**
     * Compiles a new GeneralMarksheet from all GRADED subject marksheets for
     * the given class, term, exam type, and academic year.
     *
     * <p>Returns {@code 201 Created} with the full compiled result on success.</p>
     *
     * <p>Returns {@code 409 Conflict} if a marksheet already exists for this
     * combination, or {@code 422 Unprocessable Entity} if not all subject
     * marksheets have been graded yet.</p>
     *
     * @param dto the compilation request identifying class, term, examType, and academicYear
     * @return the compiled {@link GeneralMarksheetResponseDto}
     */
    @PostMapping
    public ResponseEntity<GeneralMarksheetResponseDto> compile(
            @RequestBody GeneralMarksheetRequestDto dto) {

        SchoolClass schoolClass = schoolClassService.getSchoolClassById(dto.getSchoolClassId());
        AcademicYear academicYear = academicYearService.getById(dto.getAcademicYearId());

        GeneralMarksheet compiled = generalMarksheetService.createGeneralMarksheet(
                schoolClass,
                dto.getTerm(),
                dto.getExamType(),
                academicYear
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(generalMarksheetMapper.toDto(compiled));
    }

    // ── Lookups ──────────────────────────────────────────────────────────────

    /**
     * Returns the full compiled result for the given GeneralMarksheet ID,
     * including all student results and per-subject snapshots.
     *
     * @param id the GeneralMarksheet ID
     * @return {@code 200 OK} with the result, or {@code 404 Not Found}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GeneralMarksheetResponseDto> getById(@PathVariable Long id) {
        GeneralMarksheet gm = generalMarksheetService.getById(id);
        if (gm == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(generalMarksheetMapper.toDto(gm));
    }

    /**
     * Returns all compiled general marksheets for a given school class,
     * ordered newest-first.
     *
     * @param classId the ID of the school class
     * @return list of full response DTOs (may be empty)
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<GeneralMarksheetResponseDto>> getByClass(
            @PathVariable Long classId) {

        SchoolClass schoolClass = schoolClassService.getSchoolClassById(classId);
        List<GeneralMarksheetResponseDto> results = generalMarksheetService
                .getBySchoolClass(schoolClass)
                .stream()
                .map(generalMarksheetMapper::toDto)
                .toList();

        return ResponseEntity.ok(results);
    }

    // ── Aggregated views ─────────────────────────────────────────────────────

    /**
     * Returns lightweight summary rows for landing/listing pages.
     * Each entry contains class name, term, exam type, student count, and generation time.
     *
     * <p>Uses stored aggregate fields — does not load full result collections.</p>
     */
    @GetMapping("/landing")
    public ResponseEntity<List<GeneralMarksheetSummaryDTO>> getLandingRows() {
        return ResponseEntity.ok(generalMarksheetService.getLandingRows());
    }

    /**
     * Returns aggregate dashboard statistics:
     * total marksheets compiled, distinct classes covered, exam types covered,
     * and the timestamp of the most recent compilation.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<GeneralMarksheetDashboardDTO> getDashboardStats() {
        return ResponseEntity.ok(generalMarksheetService.getDashboardStats());
    }
}
