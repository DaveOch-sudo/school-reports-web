package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.andali.schoolreports.SchoolReportsApplication;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.event.StudentSavedEvent;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.Student;
import org.andali.schoolreports.service.SchoolClassService;
import org.andali.schoolreports.service.StudentService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Controller
public class StudentsController implements Initializable {
    private final StageManager stageManager;
    @FXML
    public TextField searchField;
    @FXML
    public Button searchBtn;
    @FXML
    public ChoiceBox<SchoolClass> classFilterChoiceBox;
    @FXML
    public Button addStudentBtn;
    @FXML
    public TableView<Student> studentTable;

    @FXML
    public TableColumn<Student, Long> idColumn;
    @FXML
    public TableColumn<Student, String> nameColumn;
    @FXML
    public TableColumn<Student, SchoolClass> classColumn;
    @FXML
    public TableColumn<Student, String> linColumn;
    @FXML
    public TableColumn<Student, Integer> ageColumn;
    @FXML
    public TableColumn<Student, String> genderColumn;

    @FXML
    public Pagination pagination;
    @FXML
    public Label statusLabel;
    @FXML
    public ProgressBar processProgress;
    @FXML
    public Button previousSceneBtn;

    private ConfigurableApplicationContext applicationContext;
    public ObservableList<Student> studentList;
    private final StudentService studentService;
    private final SchoolClassService schoolClassService;

    public StudentsController(StudentService studentService, StageManager stageManager, SchoolClassService schoolClassService) {
        this.studentService = studentService;
        this.stageManager = stageManager;
        this.schoolClassService = schoolClassService;
    }



    public void handleSearch() {
    }

    public void handleAddForm() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(applicationContext::getBean);
        loader.setLocation(getClass().getResource("/view/studentForm.fxml"));
        VBox studentForm = loader.load();

        BorderPane borderPane = (BorderPane) addStudentBtn.getScene().getRoot();
        borderPane.setLeft(studentForm);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applicationContext = SchoolReportsApplication.getContext();

        studentList = FXCollections.observableArrayList(); // initialise the list
        studentList.setAll(studentService.getAllStudents());

        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("schoolClass"));
        linColumn.setCellValueFactory(new PropertyValueFactory<>("lin"));
        ageColumn.setCellValueFactory(student ->
                new ReadOnlyObjectWrapper<>(student.getValue().getAge()));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        // returning to the dashboard scene
        previousSceneBtn.setOnAction(event -> {
           stageManager.switchSchene("/view/Dashboard.fxml", "Dashboard");
        });

        // Populate choice box
        classFilterChoiceBox.getItems().add(null);
        classFilterChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolClass sc) {
                return sc == null ? "All" : sc.getClassName();
            }

            @Override
            public SchoolClass fromString(String s) {
                return null;
            }
        });

        classFilterChoiceBox.getItems().addAll(schoolClassService.getAllSchoolClasses());
        classFilterChoiceBox.setValue(classFilterChoiceBox.getItems().get(0)); // select "All"

        // Filtered list
        FilteredList<Student> filteredData = new FilteredList<>(studentList, p -> true);
        studentTable.setItems(filteredData);


        // Search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                applyFilters(filteredData, searchField, classFilterChoiceBox)
        );

        // ChoiceBox listener
        classFilterChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                applyFilters(filteredData, searchField, classFilterChoiceBox)
        );

        // for populating suggestions
        searchField.setOnKeyReleased(event -> {
            String text = searchField.getText().toLowerCase();
            List<String> suggestions = studentList.stream()
                    .map(Student::getName)
                    .filter(name -> name.toLowerCase().contains(text))
                    .toList();
            System.out.println(suggestions); // you can populate a ListView below the TextField
        });




        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();

            // CONTEXT MENU ON TABLE
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editMenuItem = new MenuItem("Edit");
            MenuItem deleteMenuItem = new MenuItem("Delete");

            contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

//            row.setOnContextMenuRequested(event -> {
//                if(!row.isEmpty()) {
//                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
//                }
//            });

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            // ensuring the row gets selected automatically on right clicking
            row.setOnMousePressed(event -> {
                if (event.isSecondaryButtonDown() && !row.isEmpty()) {
                    if (!studentTable.getSelectionModel().getSelectedItems().contains(row.getItem())) {
                        studentTable.getSelectionModel().clearAndSelect(row.getIndex());
                    }
                }
            });

            // setting actions on the menu items for the context menu
            editMenuItem.setOnAction(event -> {
                Student student = studentTable.getSelectionModel().getSelectedItem();
                if (student != null) {
                    try {
                        editStudent(student);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // disable edit when many are selected
            editMenuItem.disableProperty().bind(
                    Bindings.size(studentTable.getSelectionModel().getSelectedItems())
                            .greaterThan(1)
            );


            deleteMenuItem.setOnAction(event -> {
                Student student = studentTable.getSelectionModel().getSelectedItem();
                if(student != null) {
                    confirmAndDeleteSelectedStudent(student);
                }
            });

            return row;
        });
        studentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void confirmAndDeleteSelectedStudent(Student student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Student");
        confirm.setHeaderText("Delete " + student.getName() + "?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                studentService.deleteStudent(student);
                studentList.remove(student);
            }
        });
    }

    private void editStudent(Student student) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(applicationContext::getBean);
        loader.setLocation(getClass().getResource("/view/studentForm.fxml"));
        VBox studentForm = loader.load();

        StudentFormController controller = loader.getController();
        controller.setEditStudent(student);

        BorderPane borderPane = (BorderPane) addStudentBtn.getScene().getRoot();
        borderPane.setLeft(studentForm);
    }

    // filter updater
    private void applyFilters(
            FilteredList<Student> filteredData,
            TextField searchField,
            ChoiceBox<SchoolClass> classFilterChoiceBox
    ) {
        filteredData.setPredicate(student -> {

            // ---- SEARCH FILTER ----
            String searchText = searchField.getText();
            boolean matchesSearch = true;

            if (searchText != null && !searchText.isBlank()) {
                matchesSearch = student.getName()
                        .toLowerCase()
                        .contains(searchText.toLowerCase());
            }

            // ---- CLASS FILTER ----
            SchoolClass selectedClass = classFilterChoiceBox.getValue();
            boolean matchesSchoolClass = true;

            if (selectedClass != null && !"All".equals(selectedClass.getClassName())) {
                if (student.getSchoolClass() == null) return false;
                matchesSchoolClass =
                        Objects.equals(
                                student.getSchoolClass().getId(),
                                selectedClass.getId()
                        );
            }

            return matchesSearch && matchesSchoolClass;
        });


    }


    public void reloadStudents(){

        studentList.setAll(studentService.getAllStudents());
    }

    @EventListener
    public void onStudentSaved(StudentSavedEvent event) {
        Platform.runLater(this::reloadStudents);
    }

    // bind the progress bar and status label to task properties
    public void bindTask(Task<?> task) {
        processProgress.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
    }

    public void unbind() {
        processProgress.progressProperty().unbind();
        statusLabel.textProperty().unbind();
    }
}
