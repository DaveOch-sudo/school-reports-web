package org.andali.schoolreportsweb.schoolclass.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolClassReguestDto {
    private String name;
    private Long schoolId;
    private Long defaultGradingScaleId;
}
