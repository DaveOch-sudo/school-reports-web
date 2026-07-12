package org.andali.schoolreportsweb.schoolclass.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolClassResponseDto {
    private Long id;
    private String name;
    private Long schoolId;
    private Long defaultGradingScaleId;
}
