package org.andali.schoolreportsweb.generalmarksheet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andali.schoolreportsweb.enums.ExamType;
import org.andali.schoolreportsweb.enums.Term;

/**
 * Request body for triggering the compilation of a new
 * {@link org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet}.
 *
 * <p>All four fields are required. Together they uniquely identify the set of
 * {@code GRADED} subject marksheets that will be compiled. The service will
 * reject the request if any subject marksheet for this combination is missing
 * or not yet graded.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheetRequestDto {

    /** ID of the school class to compile results for. */
    private Long schoolClassId;

    /** Term to compile (TERM_1, TERM_2, TERM_3). */
    private Term term;

    /** Exam type to compile (BOT, MID, EOT, TEST). */
    private ExamType examType;

    /** ID of the academic year to scope the compilation to. */
    private Long academicYearId;
}
