package org.andali.schoolreportsweb.reportrun.dto;

import org.andali.schoolreportsweb.reportrun.ReportCard;
import org.andali.schoolreportsweb.reportrun.ReportRun;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps {@link ReportRun} and {@link ReportCard} entities to their response DTOs.
 */
@Component
public class ReportRunMapper {

    public ReportRunResponseDto toDto(ReportRun run) {
        return ReportRunResponseDto.builder()
                .id(run.getId())
                .schoolClassId(run.getSchoolClass().getId())
                .schoolClassName(run.getSchoolClass().getName())
                .academicYearId(run.getAcademicYear().getId())
                .academicYearLabel(run.getAcademicYear().getLabel())
                .term(run.getTerm())
                .status(run.getStatus())
                .generalMarksheetIds(
                        run.getSourceMarksheets().stream()
                                .map(gm -> gm.getId())
                                .collect(Collectors.toSet()))
                .headteacherComment(run.getHeadteacherComment())
                .createdByUsername(run.getCreatedBy() != null ? run.getCreatedBy().getName() : null)
                .createdAt(run.getCreatedAt())
                .approvedAt(run.getApprovedAt())
                .publishedAt(run.getPublishedAt())
                .cards(run.getReportCards().stream().map(this::toCardDto).toList())
                .build();
    }

    public ReportCardResponseDto toCardDto(ReportCard card) {
        return ReportCardResponseDto.builder()
                .id(card.getId())
                .reportRunId(card.getReportRun().getId())
                .studentId(card.getStudent().getId())
                .studentName(card.getStudent().getName())
                .classTeacherComment(card.getClassTeacherComment())
                .runStatus(card.getReportRun().getStatus())
                .generatedAt(card.getGeneratedAt())
                .build();
    }

    /** Maps a list of runs without loading their cards — for lightweight listing endpoints. */
    public List<ReportRunResponseDto> toDtoList(List<ReportRun> runs) {
        return runs.stream().map(this::toDto).toList();
    }
}
