package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.model.enums.ExamType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private School school;

    @Column(nullable = false)
    private String name;

    private boolean active;

    private String logoUrl;

    /** Override school name on the report header if set */
    private String schoolNameOverride;

    private boolean showPosition;
    private boolean showAverage;
    private boolean showGrade;
    private boolean showRemark;
    private boolean showClassTeacherComment;
    private boolean showHeadteacherComment;

    private String footerText;

    /** Hex color for the report header bar, e.g. "#1a73e8" */
    private String headerColor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Which exam types to pull GeneralMarksheets from when generating a report card */
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "report_template_exams", joinColumns = @JoinColumn(name = "report_template_id"))
    @Column(name = "exam_type")
    private Set<ExamType> includedExams = new HashSet<>();
}
