package org.andali.schoolreports.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.MarksheetStatus;
import org.andali.schoolreports.model.enums.Term;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Marksheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = false)
    private SchoolSubject schoolSubject;

    @ManyToOne(optional = false)
    private SchoolClass schoolClass;

    @ManyToOne(optional = false)
    private GradingScale gradingScale;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "marksheet_id")
    private List<StudentMark> studentMarks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Term term;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    @Enumerated(EnumType.STRING)
    private MarksheetStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── Derived values (computed, not stored) ──────────────────────────

    /** Sum of all student scores in this marksheet. */
    @Transient
    public int getTotalMarks() {
        return studentMarks.stream()
                .mapToInt(StudentMark::getScore)
                .sum();
    }

    /** Average score across all students. Returns 0.0 if no marks exist. */
    @Transient
    public double getAverageMarks() {
        return studentMarks.stream()
                .mapToInt(StudentMark::getScore)
                .average()
                .orElse(0.0);
    }

    /** Highest score in this marksheet. Returns 0 if no marks exist. */
    @Transient
    public int getHighestMark() {
        return studentMarks.stream()
                .mapToInt(StudentMark::getScore)
                .max()
                .orElse(0);
    }

    /** Lowest score in this marksheet. Returns 0 if no marks exist. */
    @Transient
    public int getLowestMark() {
        return studentMarks.stream()
                .mapToInt(StudentMark::getScore)
                .min()
                .orElse(0);
    }

    /** Number of students who passed (score >= passThreshold). */
    public long getPassCount(int passThreshold) {
        return studentMarks.stream()
                .filter(m -> m.getScore() >= passThreshold)
                .count();
    }

    /** Number of students who failed (score < passThreshold). */
    public long getFailCount(int passThreshold) {
        return studentMarks.stream()
                .filter(m -> m.getScore() < passThreshold)
                .count();
    }

    // ── Convenience methods ─────────────────────────────────────────────

    public void addStudentMark(StudentMark mark) {
        mark.setMarksheet(this);
        studentMarks.add(mark);
    }

    public void removeStudentMark(StudentMark mark) {
        studentMarks.remove(mark);
        mark.setMarksheet(null);
    }
}