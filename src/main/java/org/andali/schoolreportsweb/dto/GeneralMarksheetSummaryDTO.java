package org.andali.schoolreportsweb.dto;

import lombok.*;
import org.andali.schoolreportsweb.model.enums.ExamType;
import org.andali.schoolreportsweb.model.enums.MarksheetStatus;
import org.andali.schoolreportsweb.model.enums.Term;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheetSummaryDTO {
    private Long id;

    private Long classId;
    private String className;

    private Term term;
    private ExamType examType;

    private Integer studentCount;
    private Integer subjectsCount;
    private Double classAverage;
    private MarksheetStatus status;
    private LocalDateTime generatedAt;

    public GeneralMarksheetSummaryDTO(
            Long id,
            String className,
            Term term,
            ExamType examType,
            Integer studentCount,
            Integer subjectsCount,
            Double classAverage,
            LocalDateTime generatedAt
    ) {
        this.id = id;
        this.className = className;
        this.term = term;
        this.examType = examType;
        this.studentCount = studentCount;
        this.subjectsCount = subjectsCount;
        this.classAverage = classAverage;
        this.generatedAt = generatedAt;
    }
}
