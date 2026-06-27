package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.model.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
public class StudentResponseDto {

    private Long id;

    private String admissionNumber;

    private String name;

    private Long schoolClassId;

    private String schoolClassName;

    private String lin;

    private LocalDate dob;

    private Integer age;

    private Gender gender;
}