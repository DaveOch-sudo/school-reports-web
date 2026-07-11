package org.andali.schoolreportsweb.marksheet;

import org.springframework.stereotype.Component;

@Component
public class MarksheetMapper {

    public MarksheetDto toDto(Marksheet m) {
        return MarksheetDto.builder()
                .id(m.getId())
                .name(m.getName())
                .schoolClassId(m.getSchoolClass().getId())
                .schoolClassName(m.getSchoolClass().getName())
                .schoolSubjectId(m.getSchoolSubject().getId())
                .schoolSubjectName(m.getSchoolSubject().getName())
                .academicYearId(m.getAcademicYear().getId())
                .academicYearLabel(m.getAcademicYear().getLabel())
                .gradingScaleOverrideId(m.getGradingScaleOverride() != null ? m.getGradingScaleOverride().getId() : null)
                .term(m.getTerm())
                .examType(m.getExamType())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .studentMarks(m.getStudentMarks().stream().map(sm ->
                        MarksheetDto.StudentMarkDto.builder()
                                .id(sm.getId())
                                .studentId(sm.getStudent().getId())
                                .studentName(sm.getStudent().getName())
                                .score(sm.getScore())
                                .grade(sm.getGrade())
                                .remark(sm.getRemark())
                                .build()
                ).toList())
                .build();
    }
}
