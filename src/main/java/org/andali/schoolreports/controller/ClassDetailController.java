package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.andali.schoolreports.SchoolReportsApplication;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.event.StudentSavedToClassEvent;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.SchoolSubject;
import org.andali.schoolreports.model.Student;
import org.andali.schoolreports.service.SchoolClassService;
import org.andali.schoolreports.service.SchoolSubjectService;
import org.andali.schoolreports.service.StudentService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ClassDetailController implements Initializable {

    private final StudentService studentService;
    private final SchoolSubjectService schoolSubjectService;
    private final StageManager stageManager;
    @FXML
    public Label subjectNumberLabel;
    @FXML
    public Label studentNumberLabel;
    @FXML
    public Label reportsNumberLabel;
    @FXML
    public TextField subjectSearchField;
    @FXML
    public ListView<SchoolSubject> subjectListView;
    @FXML
    public TextField studentSearchField;
    @FXML
    public ListView<Student> studentListView;
    @FXML
    public Button addStudentBtn;
    @FXML
    public Button addSubjectBtn;
    @FXML
    public Button reportsBtn;
    @FXML
    public Button backBtn;
    @FXML
    public Button gradingBtn;
    @FXML
    public Label classLabel;
    @FXML
    public TableView<Student> studentsTable;
    public TableColumn<Student, Long> idColumn = new TableColumn<>("Id");
    public TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
    public TableColumn<Student, String> genderColumn = new TableColumn<>("Gender");
    public TableColumn<Student, SchoolClass> classColumn =  new TableColumn<>("School Class");
    public TableColumn<Student,  String> linColumn = new TableColumn<>("Lin");
    public TableColumn<Student, Integer> ageColumn  = new TableColumn<>("Age");
    @FXML
    public TableView<SchoolSubject> subjectsTables;
    public TableColumn<SchoolSubject, Long> subjectIdColumn = new TableColumn<>("ID");
    public TableColumn<SchoolSubject, String> subjectNameColumn = new TableColumn<>("Name");
    public TableColumn<SchoolSubject, String> subjectDescriptionColumn =  new TableColumn<>("Description");
    public TableColumn<SchoolSubject, SchoolClass> subjectClassColumn = new TableColumn<>("School Class");

    private final SchoolClassService schoolClassService;
    private SchoolClass selectedClass;
    List<Student> classStudents;
    List<SchoolSubject> classSubjects;
    private ConfigurableApplicationContext applicationContext;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applicationContext = SchoolReportsApplication.getContext();


        // student table columns
        studentsTable.getColumns().addAll(idColumn, nameColumn, classColumn, genderColumn, ageColumn, linColumn);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("schoolClass"));
        linColumn.setCellValueFactory(new PropertyValueFactory<>("lin"));
        ageColumn.setCellValueFactory(student ->
                new ReadOnlyObjectWrapper<>(student.getValue().getAge()));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        // subjects table columns
        subjectsTables.getColumns().addAll(subjectIdColumn, subjectNameColumn, subjectDescriptionColumn, subjectClassColumn);
        subjectIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        subjectDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        subjectClassColumn.setCellValueFactory(new PropertyValueFactory<>("schoolClass"));

        backBtn.setOnAction(event -> {
            stageManager.loadView("/view/classes.fxml", "Classes");
        });

        addStudentBtn.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setControllerFactory(applicationContext::getBean);
                loader.setLocation(getClass().getResource("/view/studentForm.fxml"));
                VBox studentForm = loader.load();

                StudentFormController controller = loader.getController();
                controller.setSchoolClass(selectedClass);
                BorderPane borderPane = (BorderPane) addStudentBtn.getScene().getRoot();
                borderPane.setLeft(studentForm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    public void setSelectedClass(SchoolClass selectedClass) {
        this.selectedClass = selectedClass;
        populateView();
    }

    public void populateView() {
        classStudents = studentService.getAllBySchoolClass(selectedClass);
        classSubjects = schoolSubjectService.getAllBySchoolClass(selectedClass);

        studentNumberLabel.setText("Students: "+String.valueOf(classStudents.size()));
        classLabel.setText(selectedClass.getClassName());
        subjectNumberLabel.setText("Subjects: "+String.valueOf(classSubjects.size()));
        studentsTable.setItems(FXCollections.observableList(classStudents));
    }



    public ClassDetailController(SchoolClassService schoolClassService, StudentService studentService, SchoolSubjectService schoolSubjectService, StageManager stageManager) {
        this.schoolClassService = schoolClassService;
        this.studentService = studentService;
        this.schoolSubjectService = schoolSubjectService;
        this.stageManager = stageManager;
    }



    public void loadStudents(Event event) {
        if (classStudents == null) return;
        classStudents = studentService.getAllBySchoolClass(selectedClass);
        studentsTable.setItems(FXCollections.observableList(classStudents));
    }

    public void loadSubjects(Event event) {
        if (classSubjects == null) return;
        classSubjects = schoolSubjectService.getAllBySchoolClass(selectedClass);
        subjectsTables.setItems(FXCollections.observableList(classSubjects));
    }

    // listen to student saved to class even and update class students table
    @EventListener
    public void onStudentSaved(StudentSavedToClassEvent event) {
        Platform.runLater(this::reloadStudents);
    }

    private void reloadStudents() {
        populateView();
        studentsTable.setItems(FXCollections.observableList(classStudents));
    }
}
