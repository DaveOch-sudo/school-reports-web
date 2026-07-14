package org.andali.schoolreportsweb.generalmarksheet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.Term;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Full detail response DTO for a compiled {@link org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet}.
 *
 * <p>This is the primary response shape returned by
 * {@code GET /general-marksheet/{id}} and {@code POST /general-marksheet}.
 * It carries both class-level statistics and the full ranked student results.</p>
 *
 * <p>All data is a point-in-time snapshot — it reflects the state of the
 * marksheets at the moment of compilation and is not affected by later edits.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheetResponseDto {

    /** Database ID of the GeneralMarksheet. */
    private Long id;

    /** ID of the school class this marksheet belongs to. */
    private Long schoolClassId;

    /** Name of the school class (snapshot). */
    private String schoolClassName;

    /** ID of the academic year. */
    private Long academicYearId;

    /** Label of the academic year (e.g. "2025"). */
    private String academicYearLabel;

    /** Term in which this marksheet was compiled. */
    private Term term;

    /** Exam type (BOT, MID, EOT, TEST). */
    private ExamType examType;

    /** Timestamp when this general marksheet was compiled. */
    private LocalDateTime generatedAt;

    // ── Class-level statistics ────────────────────────────────────────────

    /** Total number of students included in this marksheet. */
    private int totalStudents;

    /** Total number of subjects included in this marksheet. */
    private int totalSubjects;

    /** Highest total score achieved by any student in the class. */
    private int classHighestTotal;

    /** Lowest total score achieved by any student in the class. */
    private int classLowestTotal;

    /** Mean total score across all students. */
    private double classAverageTotal;

    // ── Student results ───────────────────────────────────────────────────

    /**
     * Ranked list of student results, ordered by position (ascending).
     * Students with tied totals share the same position value.
     */
    private List<GeneralStudentResultDto> results;
}
