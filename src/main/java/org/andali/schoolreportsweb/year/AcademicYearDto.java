package org.andali.schoolreportsweb.year;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AcademicYearDto {
    private Long id;
    private Long schoolId;
    private String label;
    private boolean isCurrent;
    private LocalDate startDate;
    private LocalDate endDate;
}
