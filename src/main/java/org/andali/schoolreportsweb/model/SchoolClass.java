package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "className"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String className;

    @ManyToOne(optional = false)
    private School school;

    @ManyToOne
    private GradingScale defaultGradingScale;

    @Override
    public String toString() {
        return className;
    }
}
