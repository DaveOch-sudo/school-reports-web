package org.andali.schoolreportsweb.generalmarksheet.dto;

import org.andali.schoolreportsweb.generalmarksheet.GeneralMarksheet;
import org.andali.schoolreportsweb.generalmarksheet.GeneralStudentResult;
import org.andali.schoolreportsweb.generalmarksheet.SubjectResult;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Maps {@link GeneralMarksheet} entities (and their children) to response DTOs.
 *
 * <p>All mapping is done in-process from already-loaded JPA entities, so no
 * additional database queries are issued here.</p>
 */
@Component
public class GeneralMarksheetMapper {

    /**
     * Converts a {@link GeneralMarksheet} entity to a full
     * {@link GeneralMarksheetResponseDto}, including all student results
     * and per-subject snapshots.
     *
     * <p>Student results are ordered by position (ascending) in the response.</p>
     *
     * @param gm the GeneralMarksheet entity (must not be null)
     * @return the fully populated response DTO
     */
    public GeneralMarksheetResponseDto toDto(GeneralMarksheet gm) {
        List<GeneralStudentResultDto> resultDtos = gm.getResults().stream()
                .sorted(Comparator.comparingInt(GeneralStudentResult::getPosition))
                .map(this::toStudentResultDto)
                .toList();

        return GeneralMarksheetResponseDto.builder()
                .id(gm.getId())
                .schoolClassId(gm.getSchoolClass().getId())
                .schoolClassName(gm.getSchoolClass().getName())
                .academicYearId(gm.getAcademicYear().getId())
                .academicYearLabel(gm.getAcademicYear().getLabel())
                .term(gm.getTerm())
                .examType(gm.getExamType())
                .generatedAt(gm.getGeneratedAt())
                .totalStudents(gm.getTotalStudents())
                .totalSubjects(gm.getTotalSubjects())
                .classHighestTotal(gm.getClassHighestTotal())
                .classLowestTotal(gm.getClassLowestTotal())
                .classAverageTotal(gm.getClassAverageTotal())
                .results(resultDtos)
                .build();
    }

    /**
     * Converts a {@link GeneralStudentResult} to its DTO representation.
     * Subject results are ordered alphabetically by subject name for consistent display.
     */
    private GeneralStudentResultDto toStudentResultDto(GeneralStudentResult gsr) {
        List<SubjectResultDto> subjectDtos = gsr.getSubjectResults().stream()
                .sorted(Comparator.comparing(SubjectResult::getSubjectName))
                .map(this::toSubjectResultDto)
                .toList();

        return GeneralStudentResultDto.builder()
                .id(gsr.getId())
                .studentId(gsr.getStudent().getId())
                .studentName(gsr.getStudent().getName())
                .admissionNumber(gsr.getStudent().getAdmissionNumber())
                .totalMarks(gsr.getTotalMarks())
                .averageMarks(gsr.getAverageMarks())
                .position(gsr.getPosition())
                .subjectResults(subjectDtos)
                .build();
    }

    /**
     * Converts a {@link SubjectResult} snapshot to its DTO representation.
     */
    private SubjectResultDto toSubjectResultDto(SubjectResult sr) {
        return SubjectResultDto.builder()
                .id(sr.getId())
                .subjectName(sr.getSubjectName())
                .score(sr.getScore())
                .grade(sr.getGrade())
                .remark(sr.getRemark())
                .build();
    }
}
