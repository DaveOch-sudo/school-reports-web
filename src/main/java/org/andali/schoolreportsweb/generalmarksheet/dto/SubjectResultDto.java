package org.andali.schoolreportsweb.generalmarksheet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Snapshot of a single subject's result for one student within a compiled
 * {@link org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet}.
 *
 * <p>All fields are snapshotted at compilation time; they are independent of
 * any future changes to the source marksheet or subject record.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResultDto {

    /** Database ID of the SubjectResult row. */
    private Long id;

    /**
     * Name of the subject at the time of compilation.
     * Preserved even if the subject is later renamed or deleted.
     */
    private String subjectName;

    /** Raw numeric score. */
    private int score;

    /** Grade label resolved from the grading scale (e.g. "A", "B+"). */
    private String grade;

    /** Remark resolved from the grading scale (e.g. "Excellent"). */
    private String remark;
}
