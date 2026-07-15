package org.andali.schoolreportsweb.reportrun.dto;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.enums.ReportRunStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for a single {@link org.andali.schoolreportsweb.reportrun.ReportCard}.
 */
@Data
@Builder
public class ReportCardResponseDto {

    private Long id;

    private Long reportRunId;

    private Long studentId;
    private String studentName;

    private String classTeacherComment;

    /** Reflects the parent run's status — cards have no independent lifecycle. */
    private ReportRunStatus runStatus;

    private LocalDateTime generatedAt;
}
