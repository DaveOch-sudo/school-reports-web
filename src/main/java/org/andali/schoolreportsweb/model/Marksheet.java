package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.model.enums.ExamType;
import org.andali.schoolreportsweb.model.enums.MarksheetStatus;
import org.andali.schoolreportsweb.model.enums.Term;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"school_class_id", "school_subject_id", "term", "examType", "academic_year_id"}))
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
    private AcademicYear academicYear;

    /** Nullable — falls back to schoolClass.defaultGradingScale if not set */
    @ManyToOne
    private GradingScale gradingScaleOverride;

    @OneToMany(mappedBy = "marksheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentMark> studentMarks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    @Enumerated(EnumType.STRING)
    private MarksheetStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    public double getAverageMarks() {
        return studentMarks.stream().mapToInt(StudentMark::getScore).average().orElse(0.0);
    }

    @Transient
    public int getHighestMark() {
        return studentMarks.stream().mapToInt(StudentMark::getScore).max().orElse(0);
    }

    @Transient
    public int getLowestMark() {
        return studentMarks.stream().mapToInt(StudentMark::getScore).min().orElse(0);
    }

    public void addStudentMark(StudentMark mark) {
        mark.setMarksheet(this);
        studentMarks.add(mark);
    }

    public void removeStudentMark(StudentMark mark) {
        studentMarks.remove(mark);
        mark.setMarksheet(null);
    }
}
