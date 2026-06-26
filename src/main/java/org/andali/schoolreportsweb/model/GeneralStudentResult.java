package org.andali.schoolreportsweb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralStudentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    @ManyToOne(optional = false)
    private GeneralMarksheet generalMarksheet;

    private int totalMarks;
    private double averageMarks;
    private int position;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectResult> subjectResults = new ArrayList<>();
}

