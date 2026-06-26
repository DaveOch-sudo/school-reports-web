package org.andali.schoolreportsweb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarksheetRowDTO {
    private Long studentId;
    private String studentName;

    private Integer score;      // editable
    private String grade;       // derived
    private String remark;      // derived

}
