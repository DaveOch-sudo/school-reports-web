package org.andali.schoolreportsweb.student;

import jakarta.transaction.Transactional;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.schoolclass.SchoolClassService;
import org.andali.schoolreportsweb.student.dto.StudentResponseDto;
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

    /**
     * Creates a new Student from the given response DTO.
     * Maps fields manually and saves to repository.
     *
     * @param studentDto the student details DTO
     * @return the saved Student entity
     */
    public Student createStudent(StudentResponseDto studentDto) {
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

    /**
     * Finds a student by name and associated SchoolClass.
     */
    public Student getStudentByNameAndClass(String name, SchoolClass schoolClass) {
        return studentRepository.findAllByNameAndSchoolClass(name, schoolClass);
    }

    /**
     * Finds all students matching the given name.
     */
    public List<Student> getAllByName(String name) {
        return studentRepository.findByName(name);
    }

    /**
     * Finds all students enrolled in the given SchoolClass.
     */
    public List<Student> getAllBySchoolClass(SchoolClass schoolClass) {
        if (schoolClass == null) {
            throw new IllegalArgumentException("SchoolClass must not be null");
        }
        return studentRepository.findBySchoolClass_Id(schoolClass.getId());
    }

    /**
     * Deletes a student by ID.
     * Safely checks if the student exists before deletion.
     */
    public void deleteStudent(Long id) {
        studentRepository.findById(id).ifPresent(studentRepository::delete);
    }

    /**
     * Retrieves all student records.
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Performs a batch save of student entities.
     */
    public void addMany(List<Student> students) {
        studentRepository.saveAll(students);
    }

    /**
     * Updates an existing student record.
     * Ensures that the student exists before updating properties.
     *
     * @param id the ID of the student to update
     * @param editStudent the DTO containing updated student information
     * @return the updated Student entity, or null if the student was not found
     */
    @Transactional
    public Student updateStudent(Long id, StudentResponseDto editStudent) {
        // Use the id parameter to look up the student safely
        var studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            System.out.println("Student with ID " + id + " not found!");
            return null;
        }

        Student student = studentOpt.get();
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
