package org.andali.schoolreportsweb.school.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolResponseDto {
    private String id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String motto;
    private String emisCode;
    private String logoUrl;
    private String registrationNumber;
}
