package org.andali.schoolreportsweb.dto;

import lombok.Getter;
import lombok.Setter;
import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.model.SchoolSubject;
import org.andali.schoolreportsweb.model.enums.ExamType;
import org.andali.schoolreportsweb.model.enums.MarksheetStatus;
import org.andali.schoolreportsweb.model.enums.Term;

import java.time.LocalDateTime;

@Getter
@Setter
public class MarksheetsLandingDto {
    private Long marksheetId;
    private String marksheetName;
    private SchoolSubject schoolSubject;
    private SchoolClass schoolClass;
    private Term term;
    private ExamType examType;
    private MarksheetStatus status;
    private LocalDateTime lastUpdated;
    private LocalDateTime created;
}
