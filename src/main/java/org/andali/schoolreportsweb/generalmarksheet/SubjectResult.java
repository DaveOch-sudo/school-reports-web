package org.andali.schoolreportsweb.generalmarksheet;

import jakarta.persistence.*;
import lombok.*;
import org.andali.schoolreportsweb.subject.SchoolSubject;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Snapshot of subject name at compilation time — preserved even if subject is renamed/deleted */
    @Column(nullable = false)
    private String subjectName;

    private int score;
    private String grade;
    private String remark;

    /** Soft reference — nullable so historical results survive subject deletion */
    @ManyToOne
    @JoinColumn(nullable = true)
    private SchoolSubject schoolSubject;
}
