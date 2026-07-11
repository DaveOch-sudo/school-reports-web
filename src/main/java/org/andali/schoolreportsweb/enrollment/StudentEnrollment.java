package org.andali.schoolreportsweb.enrollment;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.enums.EnrollmentStatus;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.student.Student;
import org.andali.schoolreportsweb.year.AcademicYear;

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
