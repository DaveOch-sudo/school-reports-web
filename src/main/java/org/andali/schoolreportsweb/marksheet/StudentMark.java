package org.andali.schoolreportsweb.marksheet;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.student.Student;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"marksheet_id", "student_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private Marksheet marksheet;

    @Column(nullable = false)
    private int score;

    @Transient
    private String grade;

    @Transient
    private String remark;
}
