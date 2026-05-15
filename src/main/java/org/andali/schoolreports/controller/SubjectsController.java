package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.SchoolSubject;
import org.andali.schoolreports.service.SchoolClassService;
import org.andali.schoolreports.service.SchoolSubjectService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class SubjectsController implements Initializable {

    private final StageManager stageManager;
    @FXML
    public TextField searchField;
    @FXML
    public TableView<SchoolSubject> subjectsTable;
    @FXML
    public TableColumn<SchoolSubject, Long> idColumn;
    @FXML
    public TableColumn<SchoolSubject, String> nameColumn;
    @FXML
    public TableColumn<SchoolSubject, String> descriptionColumn;
    @FXML
    public TableColumn<SchoolSubject, SchoolClass> classColumn;
    @FXML
    public Button addSubjectBtn;

    @FXML
    public VBox formPanel;
    @FXML
    public ListView<SchoolSubject> quickListView;
    @FXML
    public TextField nameField;
    @FXML
    public TextField descriptionField;
    @FXML
    public ChoiceBox<SchoolClass> classChoice;
    @FXML
    public Button saveBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public Button backBtn;

    private final SchoolSubjectService schoolSubjectService;
    private final SchoolClassService schoolClassService;
    private final ApplicationEventPublisher eventPublisher;


    private SchoolSubject editSubject;

    public SubjectsController(SchoolSubjectService schoolSubjectService, SchoolClassService schoolClassService,
                              ApplicationEventPublisher eventPublisher, StageManager stageManager) {
        this.schoolSubjectService = schoolSubjectService;
        this.schoolClassService = schoolClassService;
        this.eventPublisher = eventPublisher;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Table setup
        idColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getId())
        );

        nameColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getName())
        );

        descriptionColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDescription())
        );

        classColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getSchoolClass())
        );


        // Load data
        reloadTable();

        // Quick list
        quickListView.setItems(FXCollections.observableArrayList(schoolSubjectService.getAllSubjects()));
        quickListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                loadSubjectToForm(newVal);
            }
        });

        // Class ChoiceBox
        classChoice.setItems(FXCollections.observableArrayList(schoolClassService.getAllSchoolClasses()));

        // Add button
        addSubjectBtn.setOnAction(event -> {
            editSubject = null;
            showForm();
        });

        // Save button
        saveBtn.setOnAction(event -> saveSubject());

        // Cancel button
        cancelBtn.setOnAction(event -> hideForm());

        // back button
        backBtn.setOnAction(event -> stageManager.loadView("/view/Dashboard.fxml","Dashboard"));
    }

    private void reloadTable() {
        List<SchoolSubject> subjects = schoolSubjectService.getAllSubjects();
        subjectsTable.setItems(FXCollections.observableList(subjects));
    }

    private void showForm() {
        formPanel.setVisible(true);
        formPanel.setManaged(true);
        nameField.clear();
        descriptionField.clear();
        classChoice.getSelectionModel().clearSelection();
    }

    private void hideForm() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
        editSubject = null;
    }

    private void loadSubjectToForm(SchoolSubject subject) {
        editSubject = subject;
        nameField.setText(subject.getName());
        descriptionField.setText(subject.getDescription());
        classChoice.setValue(subject.getSchoolClass());
        showForm();
    }

    private void saveSubject() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        SchoolClass selectedClass = classChoice.getValue();

        if(name.isBlank() || selectedClass == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Name and Class are required!");
            alert.showAndWait();
            return;
        }

        if(editSubject != null) {
            editSubject.setName(name);
            editSubject.setDescription(description);
            editSubject.setSchoolClass(selectedClass);
            schoolSubjectService.updateSubject(editSubject.getId(), editSubject);
        } else {
            SchoolSubject newSubject = new SchoolSubject(null, name, description, selectedClass);
            schoolSubjectService.addSchoolSubject(newSubject);
        }

        reloadTable();
        quickListView.setItems(FXCollections.observableArrayList(schoolSubjectService.getAllSubjects()));
        hideForm();
    }
}
