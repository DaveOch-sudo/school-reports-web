package org.andali.schoolreports.service;

import jakarta.transaction.Transactional;
import javafx.concurrent.Task;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.Student;
import org.andali.schoolreports.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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

    public Task<Void> createImportTask(File excelFile) {

        return new Task<>() {
            @Override
            protected Void call() throws Exception {

                updateMessage("Reading Excel file...");

                List<Student> students = new ArrayList<>();

                try (FileInputStream fis = new FileInputStream(excelFile);
                     Workbook workbook = new XSSFWorkbook(fis)) {

                    Sheet sheet = workbook.getSheetAt(0);
                    int totalRows = sheet.getLastRowNum();

                    for (int i = 1; i <= totalRows; i++) {

                        if (isCancelled()) break;

                        Row row = sheet.getRow(i);
                        if (row == null) continue;

                        Student student = new Student();
                        student.setName(row.getCell(0).getStringCellValue());
                        if (student.getName().isBlank()) continue;

                        student.setLin(row.getCell(1).getStringCellValue());

                        Cell dobCell = row.getCell(2);
                        if (dobCell != null && dobCell.getCellType() == CellType.NUMERIC) {
                            student.setDob(dobCell.getLocalDateTimeCellValue().toLocalDate());
                        }

                        student.setGender(row.getCell(3).getStringCellValue());

                        String className = row.getCell(4).getStringCellValue();
                        SchoolClass sc = schoolClassService.getSchoolClassByName(className);
                        if (sc == null) {
                            throw new RuntimeException("Unknown class: " + className);
                        }

                        student.setSchoolClass(sc);
                        students.add(student);

                        updateProgress(i, totalRows);
                        updateMessage("Importing row " + i + " of " + totalRows);
                    }
                }

                updateMessage("Saving students...");
                addMany(students);

                updateProgress(1, 1);
                updateMessage("Import complete");

                return null;
            }
        };
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
