package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"school_class_id", "lin"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private SchoolClass schoolClass;

    private String lin;

    private LocalDate dob;

    private String gender;

    @Transient
    public int getAge() {
        if (dob == null) return -1;
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
