package org.andali.schoolreportsweb.reportrun;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.enums.ReportRunStatus;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.exception.ResourceNotFoundException;
import org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet;
import org.andali.schoolreportsweb.reporttemplate.ReportTemplate;
import org.andali.schoolreportsweb.reporttemplate.ReportTemplateService;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.student.Student;
import org.andali.schoolreportsweb.student.StudentRepository;
import org.andali.schoolreportsweb.user.User;
import org.andali.schoolreportsweb.year.AcademicYear;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Service for creating and managing {@link ReportRun} instances and their
 * child {@link ReportCard}s.
 *
 * <h3>Create flow</h3>
 * <ol>
 *   <li>Validate no run already exists for (schoolClass, academicYear, term).</li>
 *   <li>Snapshot the active {@link ReportTemplate} to JSON.</li>
 *   <li>Create one {@link ReportCard} per student in the class.</li>
 *   <li>Persist everything in a single transaction.</li>
 * </ol>
 *
 * <h3>Lifecycle transitions</h3>
 * <ul>
 *   <li>{@link #approve}  — DRAFT → APPROVED. Locks all comments.</li>
 *   <li>{@link #publish}  — APPROVED → PUBLISHED. Enables PDF download.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ReportRunService {

    private final ReportRunRepository reportRunRepository;
    private final ReportCardRepository reportCardRepository;
    private final ReportTemplateService reportTemplateService;
    private final StudentRepository studentRepository;
    private final ObjectMapper objectMapper;

    // ── Create ───────────────────────────────────────────────────────────────

    /**
     * Creates a new {@link ReportRun} for the given class and term.
     *
     * <p>Snapshots the provided {@link ReportTemplate}, then generates one
     * {@link ReportCard} per student currently enrolled in the class.</p>
     *
     * @param schoolClass        the class to generate cards for
     * @param academicYear       the academic year scope
     * @param term               the term
     * @param sourceMarksheets   one or more compiled GeneralMarksheets to draw results from
     * @param template           the active ReportTemplate to snapshot
     * @param headteacherComment optional class-wide headteacher comment (may be null)
     * @param createdBy          the user initiating the run
     * @throws IllegalStateException if a run already exists for this class/year/term
     */
    @Transactional
    public ReportRun createRun(SchoolClass schoolClass,
                               AcademicYear academicYear,
                               Term term,
                               Set<GeneralMarksheet> sourceMarksheets,
                               ReportTemplate template,
                               String headteacherComment,
                               User createdBy) {

        // ── Duplicate guard ───────────────────────────────────────────────────
        if (reportRunRepository.existsBySchoolClassAndAcademicYearAndTerm(schoolClass, academicYear, term)) {
            throw new IllegalStateException(
                    "A report run already exists for class '%s', %s in academic year '%s'."
                            .formatted(schoolClass.getName(), term, academicYear.getLabel()));
        }

        // ── Snapshot the template ─────────────────────────────────────────────
        String snapshotJson = snapshotTemplate(template);

        // ── Build the run ─────────────────────────────────────────────────────
        ReportRun run = new ReportRun();
        run.setSchoolClass(schoolClass);
        run.setAcademicYear(academicYear);
        run.setTerm(term);
        run.setSourceMarksheets(sourceMarksheets);
        run.setTemplateSnapshot(snapshotJson);
        run.setHeadteacherComment(headteacherComment);
        run.setStatus(ReportRunStatus.DRAFT);
        run.setCreatedBy(createdBy);
        run.setCreatedAt(LocalDateTime.now());

        // ── Generate one ReportCard per student ───────────────────────────────
        List<Student> students = studentRepository.findBySchoolClass(schoolClass);
        if (students.isEmpty()) {
            throw new IllegalStateException(
                    "No students found in class '%s'. Add students before creating a report run."
                            .formatted(schoolClass.getName()));
        }

        LocalDateTime now = LocalDateTime.now();
        List<ReportCard> cards = students.stream().map(student -> {
            ReportCard card = new ReportCard();
            card.setReportRun(run);
            card.setStudent(student);
            card.setGeneratedAt(now);
            return card;
        }).toList();

        run.setReportCards(cards);

        return reportRunRepository.save(run);
    }

    // ── Lifecycle transitions ────────────────────────────────────────────────

    /**
     * Transitions a run from {@code DRAFT} to {@code APPROVED}.
     * Locks all comments — no further edits to teacher or headteacher comments allowed.
     *
     * @throws IllegalStateException if the run is not currently DRAFT
     */
    @Transactional
    public ReportRun approve(Long runId) {
        ReportRun run = getById(runId);
        if (run.getStatus() != ReportRunStatus.DRAFT) {
            throw new IllegalStateException(
                    "Run %d cannot be approved — current status is %s (must be DRAFT)."
                            .formatted(runId, run.getStatus()));
        }
        run.setStatus(ReportRunStatus.APPROVED);
        run.setApprovedAt(LocalDateTime.now());
        return reportRunRepository.save(run);
    }

    /**
     * Transitions a run from {@code APPROVED} to {@code PUBLISHED}.
     * Enables PDF generation and download for all cards in this run.
     *
     * @throws IllegalStateException if the run is not currently APPROVED
     */
    @Transactional
    public ReportRun publish(Long runId) {
        ReportRun run = getById(runId);
        if (run.getStatus() != ReportRunStatus.APPROVED) {
            throw new IllegalStateException(
                    "Run %d cannot be published — current status is %s (must be APPROVED)."
                            .formatted(runId, run.getStatus()));
        }
        run.setStatus(ReportRunStatus.PUBLISHED);
        run.setPublishedAt(LocalDateTime.now());
        return reportRunRepository.save(run);
    }

    // ── Comment updates ──────────────────────────────────────────────────────

    /**
     * Updates the headteacher comment on a run.
     * Only allowed while the run is {@code DRAFT}.
     */
    @Transactional
    public ReportRun updateHeadteacherComment(Long runId, String comment) {
        ReportRun run = getById(runId);
        assertDraft(run);
        run.setHeadteacherComment(comment);
        return reportRunRepository.save(run);
    }

    /**
     * Updates the class teacher comment on a single {@link ReportCard}.
     * Only allowed while the parent run is {@code DRAFT}.
     */
    @Transactional
    public ReportCard updateCardComment(Long cardId, String comment) {
        ReportCard card = reportCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("ReportCard not found: " + cardId));
        assertDraft(card.getReportRun());
        card.setClassTeacherComment(comment);
        return reportCardRepository.save(card);
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public ReportRun getById(Long id) {
        return reportRunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report run not found "+id));
    }

    public List<ReportRun> getBySchoolClass(SchoolClass schoolClass) {
        return reportRunRepository.findBySchoolClassOrderByCreatedAtDesc(schoolClass);
    }

    public List<ReportCard> getCardsByRun(Long runId) {
        ReportRun run = getById(runId);
        return reportCardRepository.findByReportRun(run);
    }

    public ReportCard getCardById(Long cardId) {
        return reportCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("ReportCard not found: " + cardId));
    }

    // ── Template snapshot helper ─────────────────────────────────────────────

    /**
     * Serialises a {@link ReportTemplate} into a {@link ReportTemplateSnapshot} JSON string.
     * Called once at run creation; never called again for that run.
     */
    public ReportTemplateSnapshot deserializeSnapshot(String json) {
        try {
            return objectMapper.readValue(json, ReportTemplateSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize template snapshot", e);
        }
    }

    private String snapshotTemplate(ReportTemplate t) {
        ReportTemplateSnapshot snapshot = ReportTemplateSnapshot.builder()
                .templateId(t.getId())
                .templateName(t.getName())
                .schoolNameOverride(t.getSchoolNameOverride())
                .logoUrl(t.getLogoUrl())
                .headerColor(t.getHeaderColor())
                .footerText(t.getFooterText())
                .showPosition(t.isShowPosition())
                .showAverage(t.isShowAverage())
                .showGrade(t.isShowGrade())
                .showRemark(t.isShowRemark())
                .showClassTeacherComment(t.isShowClassTeacherComment())
                .showHeadteacherComment(t.isShowHeadteacherComment())
                .includedExams(t.getIncludedExams())
                .build();
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize template snapshot", e);
        }
    }

    // ── Guard ────────────────────────────────────────────────────────────────

    private void assertDraft(ReportRun run) {
        if (run.getStatus() != ReportRunStatus.DRAFT) {
            throw new IllegalStateException(
                    "Run %d is %s — edits are only allowed while the run is DRAFT."
                            .formatted(run.getId(), run.getStatus()));
        }
    }
}
