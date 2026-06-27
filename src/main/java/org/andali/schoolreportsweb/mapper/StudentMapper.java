package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.StudentResponseDto;
import org.andali.schoolreportsweb.model.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentResponseDto toDto(Student student) {
        return StudentResponseDto.builder()
                .id(student.getId())
                .admissionNumber(student.getAdmissionNumber())
                .name(student.getName())
                .schoolClassId(student.getSchoolClass().getId())
                .schoolClassName(student.getSchoolClass().getName())
                .lin(student.getLin())
                .dob(student.getDob())
                .age(student.getAge())
                .gender(student.getGender())
                .build();
    }

    public Student entity(StudentResponseDto studentDto) {
        Student student = new Student();
        return student;
    }
}