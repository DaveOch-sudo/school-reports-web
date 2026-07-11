package org.andali.schoolreportsweb.school.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolUpdateRequestDto {

    private Long id;

    private String name;

    private String address;

    private String phone;

    private String email;

    private String Emis_code;

    private String motto;

    private String logoUrl;

    private String registrationNumber;
}
