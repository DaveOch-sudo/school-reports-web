package org.andali.schoolreportsweb.reportrun;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.student.Student;

import java.time.LocalDateTime;

/**
 * Represents a single student's report card within a {@link ReportRun}.
 *
 * <p>A {@code ReportCard} is always scoped to one student and one run.
 * All score/grade data is read from the run's {@link ReportRun#sourceMarksheets}
 * at PDF render time — this entity only stores the comment and metadata.</p>
 *
 * <h3>Lifecycle</h3>
 * <p>Cards do not have their own status. They follow the parent
 * {@link ReportRun#status}:</p>
 * <ul>
 *   <li>{@code DRAFT}     — {@link #classTeacherComment} is editable.</li>
 *   <li>{@code APPROVED}  — All edits locked.</li>
 *   <li>{@code PUBLISHED} — PDF can be generated and downloaded.</li>
 * </ul>
 *
 * <h3>Comments</h3>
 * <p>{@link #classTeacherComment} is individual per student. The headteacher
 * comment is class-wide and lives on {@link ReportRun#headteacherComment}.</p>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"report_run_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The run this card belongs to. Drives lifecycle status. */
    @ManyToOne(optional = false)
    private ReportRun reportRun;

    /** The student this card is for. */
    @ManyToOne(optional = false)
    private Student student;

    /**
     * Individual comment from the class teacher for this student.
     * Editable while the parent run is {@code DRAFT}; locked on {@code APPROVED}.
     */
    @Column(columnDefinition = "TEXT")
    private String classTeacherComment;

    /** Timestamp when this card was first generated (run creation time). */
    private LocalDateTime generatedAt;
}
