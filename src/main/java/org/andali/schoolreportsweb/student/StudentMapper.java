package org.andali.schoolreportsweb.student;

import org.andali.schoolreportsweb.schoolclass.SchoolClassRepository;
import org.andali.schoolreportsweb.student.dto.StudentRequestDto;
import org.andali.schoolreportsweb.student.dto.StudentResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper component to convert between Student entity and its DTOs.
 */
@Component
public class StudentMapper {

    private final SchoolClassRepository schoolClassRepository;

    @Autowired
    public StudentMapper(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    /**
     * Converts a Student entity to a StudentResponseDto.
     *
     * @param student the Student entity
     * @return the mapped StudentResponseDto
     */
    public StudentResponseDto toDto(Student student) {
        if (student == null) return null;
        return StudentResponseDto.builder()
                .id(student.getId())
                .admissionNumber(student.getAdmissionNumber())
                .name(student.getName())
                .schoolClassId(student.getSchoolClass() != null ? student.getSchoolClass().getId() : null)
                .schoolClassName(student.getSchoolClass() != null ? student.getSchoolClass().getName() : null)
                .lin(student.getLin())
                .dob(student.getDob())
                .age(student.getAge())
                .gender(student.getGender())
                .build();
    }

    /**
     * Converts a StudentResponseDto back into a Student entity.
     * Looks up the associated SchoolClass from class ID.
     *
     * @param studentDto the student response DTO
     * @return the populated Student entity
     */
    public Student entity(StudentResponseDto studentDto) {
        if (studentDto == null) return null;
        Student student = new Student();
        student.setId(studentDto.getId());
        student.setAdmissionNumber(studentDto.getAdmissionNumber());
        student.setName(studentDto.getName());
        student.setLin(studentDto.getLin());
        student.setDob(studentDto.getDob());
        student.setGender(studentDto.getGender());
        if (studentDto.getSchoolClassId() != null) {
            student.setSchoolClass(schoolClassRepository.findById(studentDto.getSchoolClassId()).orElse(null));
        }
        return student;
    }

    /**
     * Converts a StudentRequestDto to a Student entity.
     * Looks up the associated SchoolClass from class ID.
     *
     * @param requestDto the student request DTO
     * @return the populated Student entity
     */
    public Student toEntity(StudentRequestDto requestDto) {
        if (requestDto == null) return null;
        Student student = new Student();
        student.setAdmissionNumber(requestDto.getAdmissionNumber());
        student.setName(requestDto.getName());
        student.setLin(requestDto.getLin());
        student.setDob(requestDto.getDob());
        student.setGender(requestDto.getGender());
        if (requestDto.getClassId() != null) {
            student.setSchoolClass(schoolClassRepository.findById(requestDto.getClassId()).orElse(null));
        }
        return student;
    }
}