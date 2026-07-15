package org.andali.schoolreportsweb.reportrun.dto;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.enums.ReportRunStatus;
import org.andali.schoolreportsweb.enums.Term;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Full response DTO for a {@link org.andali.schoolreportsweb.reportrun.ReportRun}.
 */
@Data
@Builder
public class ReportRunResponseDto {

    private Long id;

    private Long schoolClassId;
    private String schoolClassName;

    private Long academicYearId;
    private String academicYearLabel;

    private Term term;
    private ReportRunStatus status;

    /** IDs of the GeneralMarksheets included in this run. */
    private Set<Long> generalMarksheetIds;

    private String headteacherComment;

    private String createdByUsername;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime publishedAt;

    /** All report cards generated for this run (one per student). */
    private List<ReportCardResponseDto> cards;
}
