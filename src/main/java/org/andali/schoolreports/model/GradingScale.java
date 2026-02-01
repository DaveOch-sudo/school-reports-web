package org.andali.schoolreports.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GradingScale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "gradingScale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeStep> steps = new ArrayList<>();

    public void addStep(GradeStep step) {
        step.setGradingScale(this);
        steps.add(step);
    }

    public void removeStep(GradeStep step) {
        steps.remove(step);
        step.setGradingScale(null);
    }
}
