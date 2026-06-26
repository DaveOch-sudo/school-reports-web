package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.model.enums.ExamType;
import org.andali.schoolreportsweb.model.enums.Term;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"school_class_id", "term", "examType", "academic_year_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private SchoolClass schoolClass;

    @ManyToOne(optional = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    @OneToMany(mappedBy = "generalMarksheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneralStudentResult> results = new ArrayList<>();

    private LocalDateTime generatedAt;

    // Class-level performance summary — computed and stored at compilation time
    private int classHighestTotal;
    private double classAverageTotal;
    private int classLowestTotal;
    private int totalStudents;
    private int totalSubjects;
}
