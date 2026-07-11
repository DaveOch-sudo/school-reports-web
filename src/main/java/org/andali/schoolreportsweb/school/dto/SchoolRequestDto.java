package org.andali.schoolreportsweb.school.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolRequestDto {
    private String name;
    private String email;
    private String address;
    private String phone;

}
