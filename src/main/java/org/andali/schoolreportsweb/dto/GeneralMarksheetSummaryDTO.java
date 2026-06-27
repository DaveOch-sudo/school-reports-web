package org.andali.schoolreportsweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andali.schoolreportsweb.model.enums.ExamType;
import org.andali.schoolreportsweb.model.enums.Term;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheetSummaryDTO {
    private Long id;
    private String className;
    private Term term;
    private ExamType examType;
    private Integer studentCount;
    private Integer subjectCount;
    private Double classAverage;
    private LocalDateTime generatedAt;
}
