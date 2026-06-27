package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String emisCode;
    private String motto;
    private String logoUrl;
    private String registrationNumber;
}
