package org.andali.schoolreports.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Student> students;

    @OneToMany(cascade = CascadeType.ALL)
    public List<SchoolSubject> subjects;

    @Override
    public String toString() {
        return  className;
    }
}
