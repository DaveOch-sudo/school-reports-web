package org.andali.schoolreportsweb.student.dto;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
public class StudentRequestDto {
    private String name;
    private String admissionNumber;
    private Long classId;
    private String lin;
    private LocalDate dob;
    private Gender gender;
}
