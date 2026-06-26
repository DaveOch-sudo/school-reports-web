package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "label"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AcademicYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private School school;

    /** e.g. "2025", "2025/2026" */
    @Column(nullable = false)
    private String label;

    private boolean isCurrent;

    private LocalDate startDate;

    private LocalDate endDate;
}
