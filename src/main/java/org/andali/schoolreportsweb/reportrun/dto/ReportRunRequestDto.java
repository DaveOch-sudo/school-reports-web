package org.andali.schoolreportsweb.reportrun.dto;

import lombok.Data;
import org.andali.schoolreportsweb.enums.Term;

import java.util.Set;

/**
 * Request body for creating a new {@link org.andali.schoolreportsweb.reportrun.ReportRun}.
 *
 * <p>{@code generalMarksheetIds} accepts one ID for a single-exam run or multiple
 * IDs for a combined-exam card (e.g. BOT + MID + EOT on one page).</p>
 */
@Data
public class ReportRunRequestDto {

    /** ID of the school class to generate cards for. */
    private Long schoolClassId;

    /** ID of the academic year. */
    private Long academicYearId;

    /** Term to generate cards for. */
    private Term term;

    /**
     * IDs of the GeneralMarksheets whose student results will be shown on the cards.
     * Must contain at least one entry.
     */
    private Set<Long> generalMarksheetIds;

    /** ID of the ReportTemplate to snapshot for this run's layout. */
    private Long templateId;

    /** Optional class-wide headteacher comment printed on every card. */
    private String headteacherComment;

    /** ID of the user creating this run (school admin or delegated teacher). */
    private Long createdById;
}
