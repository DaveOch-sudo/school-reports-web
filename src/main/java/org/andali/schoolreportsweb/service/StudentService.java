package org.andali.schoolreportsweb.service;

import jakarta.transaction.Transactional;
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

    @Autowired
    public StudentService(StudentRepository studentRepository, SchoolClassService schoolClassService) {
        this.studentRepository = studentRepository;
        this.schoolClassService = schoolClassService;
    }


    public void AddStudent(Student student){
        studentRepository.save(student);
        System.out.println("Student added successfully");
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

    public void deleteStudent(Student student){
        studentRepository.delete(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addMany(List<Student> students){
        studentRepository.saveAll(students);
    }

    @Transactional
    public void updateStudent(Long id, Student editStudent) {
        Student student = studentRepository.findById(editStudent.getId()).get();
         student.setName(editStudent.getName());
         student.setLin(editStudent.getLin());
         student.setDob(editStudent.getDob());
         student.setGender(editStudent.getGender());
         student.setSchoolClass(editStudent.getSchoolClass());

         studentRepository.save(student);
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
}
