package org.andali.schoolreportsweb.subject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long schoolClassId;
    private String schoolClassName;
}
