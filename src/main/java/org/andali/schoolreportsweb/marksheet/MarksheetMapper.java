package org.andali.schoolreportsweb.marksheet;

import org.andali.schoolreportsweb.enums.MarksheetStatus;
import org.andali.schoolreportsweb.schoolclass.SchoolClassRepository;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.subject.SchoolSubjectRepository;
import org.andali.schoolreportsweb.year.AcademicYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MarksheetMapper {

    public static SchoolClassRepository schoolClassRepository;
    public static SchoolSubjectRepository schoolSubjectRepository;
    public static AcademicYearRepository academicYearRepository;

    /**
     * Constructor injection to initialize static repository fields.
     * This is a workaround to support static utility mapping methods (toDto, toEntity)
     * while utilizing Spring-managed JPA repositories.
     */
    @Autowired
    public MarksheetMapper(SchoolClassRepository schoolClassRepository,
                           SchoolSubjectRepository schoolSubjectRepository,
                           AcademicYearRepository academicYearRepository) {
        MarksheetMapper.schoolClassRepository = schoolClassRepository;
        MarksheetMapper.schoolSubjectRepository = schoolSubjectRepository;
        MarksheetMapper.academicYearRepository = academicYearRepository;
    }

    /**
     * Maps a Marksheet entity to a MarksheetDto.
     * Includes mapping of the associated student marks.
     *
     * @param m the Marksheet entity
     * @return the mapped MarksheetDto
     */
    public static MarksheetDto toDto(Marksheet m) {
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

    /**
     * Maps a MarksheetDto to a Marksheet entity.
     * Looks up associated SchoolClass, SchoolSubject, and AcademicYear using static repository fields.
     * Initializes status as DRAFT and timestamps to now.
     *
     * @param dto the MarksheetDto
     * @return the mapped Marksheet entity
     */
    public static Marksheet toEntity(MarksheetDto dto) {
        Marksheet m = new Marksheet();
        m.setId(dto.getId());
        m.setName(dto.getName());
        m.setSchoolClass(schoolClassRepository.findById(dto.getSchoolClassId()).orElse(null));
        m.setSchoolSubject(schoolSubjectRepository.findById(dto.getSchoolSubjectId()).orElse(null));
        m.setAcademicYear(academicYearRepository.findById(dto.getAcademicYearId()).orElse(null));
        m.setTerm(dto.getTerm());
        m.setExamType(dto.getExamType());
        m.setStatus(MarksheetStatus.DRAFT);
        m.setCreatedAt(LocalDateTime.now());
        m.setUpdatedAt(LocalDateTime.now());

        return m;
    }
}
