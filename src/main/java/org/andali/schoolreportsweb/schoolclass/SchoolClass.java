package org.andali.schoolreportsweb.schoolclass;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.gradingscale.GradingScale;
import org.andali.schoolreportsweb.school.School;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "name"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private School school;

    @ManyToOne
    private GradingScale defaultGradingScale;

    @Override
    public String toString() {
        return name;
    }
}
