package org.andali.schoolreportsweb.reportrun;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andali.schoolreportsweb.enums.ExamType;

import java.util.Set;

/**
 * Immutable snapshot of a {@link org.andali.schoolreportsweb.reporttemplate.ReportTemplate}
 * captured at {@link ReportRun} creation time.
 *
 * <p>Stored as a JSON blob in {@link ReportRun#templateSnapshot}. This decouples
 * published report cards from future edits to the live template — once a run is
 * created, its layout is frozen here regardless of what the school later changes
 * in their template designer.</p>
 *
 * <p>The source-of-truth template ID is included so the UI can display which
 * template was active at the time the run was created.</p>
 *
 * <p>Deserialise with {@link com.fasterxml.jackson.databind.ObjectMapper} only —
 * never cast or map directly to {@link org.andali.schoolreportsweb.reporttemplate.ReportTemplate}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportTemplateSnapshot {

    /** ID of the original ReportTemplate this snapshot was taken from. */
    private Long templateId;

    /** Name of the template at snapshot time. */
    private String templateName;

    // ── School branding ──────────────────────────────────────────────────────

    /** School name override printed on the report header; falls back to the real school name if null. */
    private String schoolNameOverride;

    /** URL of the school logo to embed in the report header. */
    private String logoUrl;

    /** Hex colour for the report header bar, e.g. {@code "#1a73e8"}. */
    private String headerColor;

    /** Footer text printed at the bottom of each report card page. */
    private String footerText;

    // ── Layout flags ─────────────────────────────────────────────────────────

    /** Whether to print the student's class position on the card. */
    private boolean showPosition;

    /** Whether to print the student's average score on the card. */
    private boolean showAverage;

    /** Whether to print the grade label (e.g. "A", "B+") per subject. */
    private boolean showGrade;

    /** Whether to print the remark (e.g. "Excellent") per subject. */
    private boolean showRemark;

    /** Whether the class teacher comment section is visible on the card. */
    private boolean showClassTeacherComment;

    /** Whether the headteacher comment section is visible on the card. */
    private boolean showHeadteacherComment;

    // ── Exam scope ───────────────────────────────────────────────────────────

    /**
     * Which exam types are included in this report layout.
     * Single-exam runs will have one entry; combined EOT runs may have BOT + MID + EOT.
     */
    private Set<ExamType> includedExams;
}
