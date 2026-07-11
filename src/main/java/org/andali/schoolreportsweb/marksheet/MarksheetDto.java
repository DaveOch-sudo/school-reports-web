package org.andali.schoolreportsweb.marksheet;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.MarksheetStatus;
import org.andali.schoolreportsweb.enums.Term;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MarksheetDto {
    private Long id;
    private String name;
    private Long schoolClassId;
    private String schoolClassName;
    private Long schoolSubjectId;
    private String schoolSubjectName;
    private Long academicYearId;
    private String academicYearLabel;
    private Long gradingScaleOverrideId;
    private Term term;
    private ExamType examType;
    private MarksheetStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<StudentMarkDto> studentMarks;

    @Data
    @Builder
    public static class StudentMarkDto {
        private Long id;
        private Long studentId;
        private String studentName;
        private int score;
        private String grade;
        private String remark;
    }
}
