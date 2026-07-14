package org.andali.schoolreportsweb.generalmarksheet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO representing one student's compiled result within a
 * {@link org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet}.
 *
 * <p>Contains the student's identity, aggregate performance figures, class
 * position, and a per-subject breakdown.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralStudentResultDto {

    /** Database ID of the GeneralStudentResult row. */
    private Long id;

    /** ID of the student. */
    private Long studentId;

    /** Full name of the student. */
    private String studentName;

    /** Admission number of the student. */
    private String admissionNumber;

    /** Sum of all subject scores. */
    private int totalMarks;

    /** Average score across all subjects, rounded to 2 decimal places. */
    private double averageMarks;

    /**
     * Class position based on total marks.
     * Tie-aware: students with equal totals share the same position.
     */
    private int position;

    /**
     * Per-subject result snapshots, ordered by subject name.
     */
    private List<SubjectResultDto> subjectResults;
}
