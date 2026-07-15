package org.andali.schoolreportsweb.reportrun;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet;
import org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheetService;
import org.andali.schoolreportsweb.reportrun.dto.ReportRunMapper;
import org.andali.schoolreportsweb.reportrun.dto.ReportRunRequestDto;
import org.andali.schoolreportsweb.reportrun.dto.ReportRunResponseDto;
import org.andali.schoolreportsweb.reporttemplate.ReportTemplate;
import org.andali.schoolreportsweb.reporttemplate.ReportTemplateService;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.user.User;
import org.andali.schoolreportsweb.user.UserService;
import org.andali.schoolreportsweb.year.AcademicYear;
import org.andali.schoolreportsweb.year.AcademicYearService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for {@link ReportRun} operations.
 *
 * <p>Base path: {@code /api/v1/report-runs}</p>
 *
 * <ul>
 *   <li>{@code POST   /}                    — Create a new run (snapshots template, generates cards)</li>
 *   <li>{@code GET    /{id}}                — Fetch run by ID (includes all cards)</li>
 *   <li>{@code GET    /class/{classId}}     — All runs for a class</li>
 *   <li>{@code PATCH  /{id}/approve}        — DRAFT → APPROVED (locks comments)</li>
 *   <li>{@code PATCH  /{id}/publish}        — APPROVED → PUBLISHED (enables PDF download)</li>
 *   <li>{@code PATCH  /{id}/headteacher-comment} — Update class-wide headteacher comment</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/report-runs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportRunController {

    private final ReportRunService reportRunService;
    private final SchoolClassService schoolClassService;
    private final AcademicYearService academicYearService;
    private final ReportTemplateService reportTemplateService;
    private final GeneralMarksheetService generalMarksheetService;
    private final UserService userService;
    private final ReportRunMapper mapper;

    /**
     * Creates a new ReportRun.
     * Snapshots the chosen template, then generates one ReportCard per student in the class.
     * Returns {@code 409 Conflict} if a run already exists for this class/year/term.
     */
    @PostMapping
    public ResponseEntity<ReportRunResponseDto> create(@RequestBody ReportRunRequestDto dto) {
        SchoolClass schoolClass = schoolClassService.getSchoolClassById(dto.getSchoolClassId());
        AcademicYear academicYear = academicYearService.getById(dto.getAcademicYearId());
        ReportTemplate template = reportTemplateService.getById(dto.getTemplateId());
        User createdBy = userService.getById(dto.getCreatedById());

        Set<GeneralMarksheet> marksheets = dto.getGeneralMarksheetIds().stream()
                .map(generalMarksheetService::getById)
                .collect(Collectors.toSet());

        ReportRun run = reportRunService.createRun(
                schoolClass, academicYear, dto.getTerm(),
                marksheets, template,
                dto.getHeadteacherComment(), createdBy);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(run));
    }

    /** Fetch a run by ID, including all its report cards. */
    @GetMapping("/{id}")
    public ResponseEntity<ReportRunResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(reportRunService.getById(id)));
    }

    /** All runs for a class, newest first. */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ReportRunResponseDto>> getByClass(@PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.getSchoolClassById(classId);
        return ResponseEntity.ok(mapper.toDtoList(reportRunService.getBySchoolClass(schoolClass)));
    }

    /**
     * Approve a run: DRAFT → APPROVED.
     * Locks all teacher and headteacher comments.
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ReportRunResponseDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(reportRunService.approve(id)));
    }

    /**
     * Publish a run: APPROVED → PUBLISHED.
     * Enables PDF generation and download for all cards in the run.
     */
    @PatchMapping("/{id}/publish")
    public ResponseEntity<ReportRunResponseDto> publish(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(reportRunService.publish(id)));
    }

    /**
     * Update the class-wide headteacher comment.
     * Only allowed while the run is DRAFT.
     */
    @PatchMapping("/{id}/headteacher-comment")
    public ResponseEntity<ReportRunResponseDto> updateHeadteacherComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                mapper.toDto(reportRunService.updateHeadteacherComment(id, body.get("comment"))));
    }
}
