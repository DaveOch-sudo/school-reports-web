package org.andali.schoolreportsweb.reportrun;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.enums.ReportRunStatus;
import org.andali.schoolreportsweb.enums.Term;
import org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.user.User;
import org.andali.schoolreportsweb.year.AcademicYear;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a report-card generation run for a single class and term.
 *
 * <h3>What a ReportRun is</h3>
 * <p>A {@code ReportRun} is the administrative decision to produce report cards for
 * a class. The school admin (or delegated teacher) selects:</p>
 * <ul>
 *   <li>Which class and term to generate cards for.</li>
 *   <li>Which {@link GeneralMarksheet}(s) to draw results from — one for a
 *       single-exam card (e.g. BOT), or multiple for a combined card (e.g. BOT + MID + EOT).</li>
 *   <li>Which {@link org.andali.schoolreportsweb.reporttemplate.ReportTemplate} to use for layout.</li>
 * </ul>
 *
 * <h3>Template snapshot</h3>
 * <p>At creation time the active template's fields are serialised to JSON and stored
 * in {@link #templateSnapshot}. This freezes the layout so future template edits by
 * the school do not retroactively change published cards.</p>
 *
 * <h3>Lifecycle</h3>
 * <pre>
 *   DRAFT → APPROVED → PUBLISHED
 * </pre>
 * <ul>
 *   <li>{@link ReportRunStatus#DRAFT}     — cards created; class teacher comments editable.</li>
 *   <li>{@link ReportRunStatus#APPROVED}  — headteacher signed off; all edits locked.</li>
 *   <li>{@link ReportRunStatus#PUBLISHED} — PDFs can be generated and downloaded.</li>
 * </ul>
 *
 * <h3>Comments</h3>
 * <p>{@link #headteacherComment} is a single class-wide comment printed on every card
 * in the run. Per-student class teacher comments live on each {@link ReportCard}.</p>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"school_class_id", "academic_year_id", "term"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Scope ────────────────────────────────────────────────────────────────

    @ManyToOne(optional = false)
    private SchoolClass schoolClass;

    @ManyToOne(optional = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Term term;

    // ── Source data ──────────────────────────────────────────────────────────

    /**
     * The GeneralMarksheet(s) whose student results are used when rendering cards.
     * Single entry for single-exam runs; multiple entries for combined-exam cards
     * (e.g. EOT card showing BOT + MID + EOT columns).
     */
    @ManyToMany
    @JoinTable(
            name = "report_run_marksheets",
            joinColumns = @JoinColumn(name = "report_run_id"),
            inverseJoinColumns = @JoinColumn(name = "general_marksheet_id")
    )
    private Set<GeneralMarksheet> sourceMarksheets = new HashSet<>();

    // ── Template snapshot ────────────────────────────────────────────────────

    /**
     * JSON serialisation of the {@link org.andali.schoolreportsweb.reporttemplate.ReportTemplate}
     * that was active when this run was created.
     *
     * <p>Stored as TEXT so future template edits cannot alter published cards.
     * Deserialise to {@link ReportTemplateSnapshot} using Jackson before rendering PDFs.</p>
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String templateSnapshot;

    // ── Comments ─────────────────────────────────────────────────────────────

    /**
     * A single class-wide comment from the headteacher, printed on every card in
     * this run. Editable while status is {@link ReportRunStatus#DRAFT}; locked on APPROVED.
     */
    @Column(columnDefinition = "TEXT")
    private String headteacherComment;

    // ── Cards ────────────────────────────────────────────────────────────────

    /**
     * One {@link ReportCard} per student in the class, generated at run creation.
     * All cards in this run share the same lifecycle — they are approved and
     * published together.
     */
    @OneToMany(mappedBy = "reportRun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportCard> reportCards = new ArrayList<>();

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportRunStatus status;

    // ── Audit ────────────────────────────────────────────────────────────────

    /** The user (school admin or delegated teacher) who initiated this run. */
    @ManyToOne
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime publishedAt;
}
