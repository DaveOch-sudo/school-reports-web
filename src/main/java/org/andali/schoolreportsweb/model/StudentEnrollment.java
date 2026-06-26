package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.model.enums.EnrollmentStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private SchoolClass schoolClass;

    @ManyToOne(optional = false)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
}
