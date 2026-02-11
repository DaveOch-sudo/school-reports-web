package org.andali.schoolreports.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private SchoolClass schoolClass;

    @Enumerated(EnumType.STRING)
    private Term term;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    @OneToMany(mappedBy = "generalMarksheet",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<GeneralStudentResult> results = new ArrayList<>();

    private LocalDateTime generatedAt;
}
