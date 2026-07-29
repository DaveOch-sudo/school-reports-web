package org.andali.schoolreportsweb.generalmarksheet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.Term;

import java.time.LocalDateTime;

@Data
@Builder
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
