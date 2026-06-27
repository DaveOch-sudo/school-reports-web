package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolSubjectDto {
    private Long id;
    private String name;
    private String description;
    private Long schoolClassId;
    private String schoolClassName;
}
