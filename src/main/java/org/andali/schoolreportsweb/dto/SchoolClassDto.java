package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolClassDto {
    private Long id;
    private String name;
    private Long schoolId;
    private String schoolName;
    private Long defaultGradingScaleId;
    private String defaultGradingScaleName;
}
