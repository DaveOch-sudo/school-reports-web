package org.andali.schoolreportsweb.service;

import jakarta.transaction.Transactional;
import org.andali.schoolreportsweb.dto.StudentResponseDto;
import org.andali.schoolreportsweb.mapper.StudentMapper;
import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.model.Student;
import org.andali.schoolreportsweb.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassService schoolClassService;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentService(StudentRepository studentRepository, SchoolClassService schoolClassService, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.schoolClassService = schoolClassService;
        this.studentMapper = studentMapper;
    }


    public Student createStudent(StudentResponseDto studentDto){
        Student student = new Student(
                studentDto.getId(),
                studentDto.getAdmissionNumber(),
                studentDto.getName(),
                schoolClassService.getSchoolClassById(studentDto.getSchoolClassId()),
                studentDto.getLin(),
                studentDto.getDob(),
                studentDto.getGender()
        );


        studentRepository.save(student);
        System.out.println("Student added successfully");
        return student;
    }

    public Student getStudentByNameAndClass(String name, SchoolClass schoolClass){
        return studentRepository.findAllByNameAndSchoolClass(name, schoolClass);
    }

    public List<Student> getAllByName(String name){
        return studentRepository.findByName(name);
    }

    public List<Student> getAllBySchoolClass(SchoolClass schoolClass){
        if (schoolClass == null) {
            throw new IllegalArgumentException("SchoolClass must not be null");
        }
        return studentRepository.findBySchoolClass_Id(schoolClass.getId());
    }

    public void deleteStudent(Long id){
        Student student = studentRepository.findById(id).get();

        // check if student exits in database before deleting
        if (student != null) {
            studentRepository.delete(student);
        }
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addMany(List<Student> students){
        studentRepository.saveAll(students);
    }

    @Transactional
    public Student updateStudent(Long id, StudentResponseDto editStudent) {
        Student student = studentRepository.findById(editStudent.getId()).get();

        if (!studentRepository.findById(editStudent.getId()).isPresent()) {
            System.out.println("Student "+editStudent.getId()+" not found!");
            return null;
        }

        SchoolClass schoolClass = schoolClassService.getSchoolClassById(editStudent.getSchoolClassId());
         student.setName(editStudent.getName());
         student.setLin(editStudent.getLin());
         student.setDob(editStudent.getDob());
         student.setGender(editStudent.getGender());
         student.setSchoolClass(schoolClass);

         studentRepository.save(student);
        return student;
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
}
