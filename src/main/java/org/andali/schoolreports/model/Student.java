package org.andali.schoolreports.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private SchoolClass schoolClass;

    private String lin;

    private LocalDate dob;

    private String gender;

    @Transient
    public int getAge() {
        if (dob == null) return -1;
        return Period.between(dob, LocalDate.now()).getYears();
    }
    @OneToMany(cascade = CascadeType.ALL)
    private List<Report> reports;
}
