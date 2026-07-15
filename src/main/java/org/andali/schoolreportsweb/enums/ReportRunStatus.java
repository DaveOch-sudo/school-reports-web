package org.andali.schoolreportsweb.enums;

/**
 * Lifecycle status of a {@link org.andali.schoolreportsweb.reportrun.ReportRun}.
 *
 * <ul>
 *   <li>{@link #DRAFT}     — Run created; per-student teacher comments can still be edited.</li>
 *   <li>{@link #APPROVED}  — Headteacher has signed off; comments are locked, no further edits allowed.</li>
 *   <li>{@link #PUBLISHED} — Report cards are released; PDFs can be generated and downloaded.</li>
 * </ul>
 */
public enum ReportRunStatus {
    DRAFT,
    APPROVED,
    PUBLISHED
}
