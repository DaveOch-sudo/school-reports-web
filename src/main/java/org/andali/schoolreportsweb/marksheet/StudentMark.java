package org.andali.schoolreportsweb.marksheet;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.student.Student;

/**
 * Represents a single student's score on a specific subject marksheet.
 *
 * <p>The {@code grade} and {@code remark} fields are resolved by
 * {@link MarksheetService#resolveAllGrades(Marksheet)} and stored as real columns
 * so that compiled GeneralMarksheet snapshots can read them back from the database
 * without needing to re-run grade resolution.</p>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"marksheet_id", "student_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private Marksheet marksheet;

    /** Raw numeric score entered by the teacher. */
    @Column(nullable = false)
    private int score;

    /**
     * Grade label resolved from the active GradingScale (e.g. "A", "B+").
     * Populated by {@link MarksheetService#resolveAllGrades(Marksheet)} and
     * persisted so it survives across sessions without re-resolution.
     */
    @Column
    private String grade;

    /**
     * Remark resolved from the active GradingScale (e.g. "Excellent", "Pass").
     * Persisted alongside {@code grade} for the same reason.
     */
    @Column
    private String remark;
}
