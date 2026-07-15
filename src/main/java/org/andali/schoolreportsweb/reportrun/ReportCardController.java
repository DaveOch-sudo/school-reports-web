package org.andali.schoolreportsweb.reportrun;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.reportrun.dto.ReportCardResponseDto;
import org.andali.schoolreportsweb.reportrun.dto.ReportRunMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for {@link ReportCard} operations.
 *
 * <p>Base path: {@code /api/v1/report-cards}</p>
 *
 * <ul>
 *   <li>{@code GET    /{id}}               — Fetch a single card by ID</li>
 *   <li>{@code GET    /run/{runId}}         — All cards for a run</li>
 *   <li>{@code PATCH  /{id}/comment}        — Update class teacher comment (DRAFT only)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/report-cards")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportRunService reportRunService;
    private final ReportRunMapper mapper;

    /** Fetch a single report card by its ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ReportCardResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toCardDto(reportRunService.getCardById(id)));
    }

    /** All report cards for a given run. */
    @GetMapping("/run/{runId}")
    public ResponseEntity<List<ReportCardResponseDto>> getByRun(@PathVariable Long runId) {
        List<ReportCardResponseDto> cards = reportRunService.getCardsByRun(runId)
                .stream().map(mapper::toCardDto).toList();
        return ResponseEntity.ok(cards);
    }

    /**
     * Update the class teacher comment on a single report card.
     * Only allowed while the parent run is in {@code DRAFT} status.
     * Returns {@code 400} if the run is APPROVED or PUBLISHED.
     *
     * <p>Request body: {@code { "comment": "..." }}</p>
     */
    @PatchMapping("/{id}/comment")
    public ResponseEntity<ReportCardResponseDto> updateComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        ReportCard updated = reportRunService.updateCardComment(id, body.get("comment"));
        return ResponseEntity.ok(mapper.toCardDto(updated));
    }
}
