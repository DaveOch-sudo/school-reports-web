package org.andali.schoolreports.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import lombok.Setter;
import org.andali.schoolreports.event.StudentSavedEvent;
import org.andali.schoolreports.event.StudentSavedToClassEvent;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.Student;
import org.andali.schoolreports.service.SchoolClassService;
import org.andali.schoolreports.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@Scope("prototype")
public class StudentFormController implements Initializable {
    @FXML
    public TextField nameField;
    @FXML
    public ChoiceBox<SchoolClass> classChoice;
    @FXML
    public DatePicker dobChooser;
    @FXML
    public TextField linField;
    @FXML
    public Button saveBtn;
    @FXML
    public TextField batchPathField;
    @FXML
    public Button batchAddBtn;
    @FXML
    public Button cancleBtn;
    @FXML
    public ChoiceBox<String> genderChoice;

    private final SchoolClassService schoolClassService;
    private final StudentService studentService;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private StudentsController studentsController;


    private  Student editStudent;
    private SchoolClass schoolClass;

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        if (schoolClass != null) {
            classChoice.getSelectionModel().select(schoolClass);
            classChoice.setDisable(true); // disable the class drop down
        }
    }

    public StudentFormController(SchoolClassService schoolClassService, StudentService studentService, ApplicationEventPublisher applicationEventPublisher) {
        this.schoolClassService = schoolClassService;
        this.studentService = studentService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setEditStudent(Student editStudent) {
        this.editStudent = editStudent;
        if (editStudent != null) {
            nameField.setText(editStudent.getName());
            dobChooser.setValue(editStudent.getDob());
            genderChoice.setValue(editStudent.getGender());
            classChoice.getSelectionModel().select(editStudent.getSchoolClass());
            linField.setText(editStudent.getLin());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<SchoolClass> schoolClasses = FXCollections.observableArrayList(schoolClassService.getAllSchoolClasses());
        classChoice.setItems(schoolClasses);


        if(!schoolClasses.isEmpty()){
            classChoice.setValue(null);
        }

        genderChoice.setItems(FXCollections.observableList(List.of("Male", "Female")));

    }

    public void handleCancle() {
       var borderPane = (BorderPane) cancleBtn.getScene().getRoot();
       borderPane.setLeft(null);
    }

    public void handleSave() {
        String name = nameField.getText();
        String lin = linField.getText().trim();
        LocalDate dob = dobChooser.getValue();
        var schoolClass =  classChoice.getValue();
        var gender = genderChoice.getValue();

        if (editStudent != null){
            editStudent.setName(name);
            editStudent.setDob(dob);
            editStudent.setGender(gender);
            editStudent.setLin(lin);
            editStudent.setSchoolClass(schoolClass);

            studentService.updateStudent(editStudent.getId(), editStudent);

        } else {
            var student = new Student(null, name, schoolClass, lin, dob, gender,null);
            studentService.AddStudent(student);
            // publish event to refresh table
            if (schoolClass == null) {
                applicationEventPublisher.publishEvent(new StudentSavedEvent(student));
            } else {
                applicationEventPublisher.publishEvent(new StudentSavedToClassEvent(student));
            }

        }

        // clear values
        nameField.clear();
        linField.clear();
        dobChooser.setValue(null);
        classChoice.getSelectionModel().select(0);


        Alert saveAlert = new Alert(Alert.AlertType.INFORMATION);
        saveAlert.setTitle("Student Saved");
        saveAlert.setHeaderText("Student Saved");
        saveAlert.setContentText("Student saved successfully");
        saveAlert.show();

    }

    //
    public void handleBatch() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Students");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // alert can be implemented with task threads
        Alert importAlert = new Alert(Alert.AlertType.INFORMATION);
        importAlert.setTitle("Import Format");
        importAlert.setHeaderText("Excel worksheet must follow this format!");
        importAlert.setContentText("Name | LIN | Dob | Gender | Class");
        importAlert.showAndWait();

        if(importAlert.getResult() != ButtonType.OK) return;
        File excel = fileChooser.showOpenDialog(this.batchPathField.getScene().getWindow());
        if (excel == null) return;

        Task<Void> importTask = studentService.createImportTask(excel);
        studentsController.bindTask(importTask);
        importTask.setOnSucceeded(e -> {
            studentsController.unbind();
            importAlert.setTitle("Import Successful");
            importAlert.setHeaderText("Student Import Successful");
            importAlert.setContentText("Students have been imported successfully");
            importAlert.show();
        });
        importTask.setOnFailed(e -> {
            studentsController.unbind();
            importAlert.setTitle("Import Error");
            importAlert.setHeaderText("Import failed");
            importAlert.setContentText("Students could not be imported successfully");
            importAlert.setAlertType(Alert.AlertType.ERROR);
            importAlert.show();
        });

        new Thread(importTask).start();

    }


//    public List<Student> readStudentsFromExcel(File file) throws FileNotFoundException {
//          List<Student> students = new ArrayList<>();
//
//          try(FileInputStream fis = new FileInputStream(file);
//              Workbook workbook = new XSSFWorkbook(fis)) {
//
//              Sheet sheet = workbook.getSheetAt(0);
//
//              // loop through all rows
//              for (int i = 1;  i <= sheet.getLastRowNum(); i++) {
//                  Row row = sheet.getRow(i);
//                  if (row == null) continue; // skip row if no values found
//
//
//                  Student student = new Student();
//                  student.setName(sheet.getRow(i).getCell(0).getStringCellValue());
//                  student.setLin(sheet.getRow(i).getCell(1).getStringCellValue());
//
//                  // skip rows with no student names
//                  if (student.getName() == null || student.getName().isBlank()) {
//                      System.out.println("Student name is null or empty at row "+ i+1);
//                      continue;
//                  }
//
//                  // DOB
//                  Cell dobCell = row.getCell(2);
//                  if (dobCell.getCellType() == CellType.NUMERIC) {
//                      student.setDob(
//                              dobCell.getLocalDateTimeCellValue().toLocalDate()
//                      );
//                  }
//
//                  student.setGender(row.getCell(3).getStringCellValue());
//
//                  // class ( lookup by name )
//                  String className = row.getCell(4).getStringCellValue();
//                  SchoolClass schoolClass = schoolClassService.getSchoolClassByName(className);
//                  if( schoolClass == null){
//                      throw new RuntimeException("School class not found");
//                  }
//
//                  student.setSchoolClass(schoolClass);
//                  students.add(student);
//              }
//          } catch (IOException e) {
//              throw new RuntimeException(e);
//          }
//
//          return students;
//    }

}
