package org.andali.schoolreports.controller.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import lombok.Getter;
import org.andali.schoolreports.dto.GradeStepRowDTO;
import org.andali.schoolreports.model.GradeStep;
import org.andali.schoolreports.model.GradingScale;
import org.andali.schoolreports.service.GradingScaleService;
import org.springframework.stereotype.Controller;

@Controller
public class GradingSystemDialogController {

    private final GradingScaleService gradingScaleService;

    @FXML private TextField gradingScaleNameField;
    @FXML private TableView<GradeStepRowDTO> gradeStepsTable;
    @FXML private TableColumn<GradeStepRowDTO, String> gradeColumn;
    @FXML private TableColumn<GradeStepRowDTO, Integer> minScoreColumn;
    @FXML private TableColumn<GradeStepRowDTO, Integer> maxScoreColumn;
    @FXML private TableColumn<GradeStepRowDTO, String> remarkColumn;
    @FXML private Button addStepBtn;
    @FXML private Button removeStepBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final ObservableList<GradeStepRowDTO> gradeStepData = FXCollections.observableArrayList();
    // Called by parent controller to get the saved grading scale
    @Getter
    private GradingScale savedGradingScale; // To pass back to parent controller

    public GradingSystemDialogController(GradingScaleService gradingScaleService) {
        this.gradingScaleService = gradingScaleService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        makeTableEditable();
        gradeStepsTable.setItems(gradeStepData);
    }

    private void setupTableColumns() {
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        minScoreColumn.setCellValueFactory(new PropertyValueFactory<>("minScore"));
        maxScoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        remarkColumn.setCellValueFactory(new PropertyValueFactory<>("remark"));
    }

    private void makeTableEditable() {
        gradeStepsTable.setEditable(true);

        // Grade column - editable text
        gradeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gradeColumn.setOnEditCommit(event -> {
            event.getRowValue().setGrade(event.getNewValue());
        });

        // Min score column - editable integer
        minScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        minScoreColumn.setOnEditCommit(event -> {
            event.getRowValue().setMinScore(event.getNewValue());
        });

        // Max score column - editable integer
        maxScoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        maxScoreColumn.setOnEditCommit(event -> {
            event.getRowValue().setMaxScore(event.getNewValue());
        });

        // Remark column - editable text
        remarkColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        remarkColumn.setOnEditCommit(event -> {
            event.getRowValue().setRemark(event.getNewValue());
        });
    }

    @FXML
    public void handleAddStep(ActionEvent actionEvent) {
        GradeStepRowDTO newStep = new GradeStepRowDTO();
        newStep.setGrade("A");
        newStep.setMinScore(0);
        newStep.setMaxScore(100);
        newStep.setRemark("Excellent");
        gradeStepData.add(newStep);
    }

    @FXML
    public void handleRemoveStep(ActionEvent actionEvent) {
        GradeStepRowDTO selected = gradeStepsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            gradeStepData.remove(selected);
        } else {
            showAlert("Remove Step", "Please select a step to remove.");
        }
    }

    @FXML
    public void handleSave(ActionEvent actionEvent) {
        String name = gradingScaleNameField.getText();

        // Validation
        if (name == null || name.trim().isEmpty()) {
            showAlert("Validation", "Please enter a grading system name.");
            return;
        }

        if (gradeStepData.isEmpty()) {
            showAlert("Validation", "Please add at least one grade step.");
            return;
        }

        // Check for overlapping or invalid ranges
        for (int i = 0; i < gradeStepData.size(); i++) {
            GradeStepRowDTO step = gradeStepData.get(i);

            if (step.getMinScore() == null || step.getMaxScore() == null) {
                showAlert("Validation", "All grade steps must have min and max scores.");
                return;
            }

            if (step.getMinScore() > step.getMaxScore()) {
                showAlert("Validation", "Min score cannot be greater than max score for grade: " + step.getGrade());
                return;
            }

            // Check for overlaps with other steps
            for (int j = i + 1; j < gradeStepData.size(); j++) {
                GradeStepRowDTO other = gradeStepData.get(j);
                if (rangesOverlap(step, other)) {
                    showAlert("Validation", "Grade ranges cannot overlap: " + step.getGrade() + " and " + other.getGrade());
                    return;
                }
            }
        }

        // Build the GradingScale entity
        GradingScale gradingScale = new GradingScale();
        gradingScale.setName(name);

        for (GradeStepRowDTO dto : gradeStepData) {
            GradeStep step = new GradeStep();
            step.setGrade(dto.getGrade());
            step.setMinScore(dto.getMinScore());
            step.setMaxScore(dto.getMaxScore());
            step.setRemark(dto.getRemark());
            gradingScale.addStep(step);
        }

        // Save to database
        savedGradingScale = gradingScaleService.addGradingScale(gradingScale);
        System.out.println(savedGradingScale);

        // Close dialog
        closeDialog();
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        closeDialog();
    }

    private boolean rangesOverlap(GradeStepRowDTO a, GradeStepRowDTO b) {
        return !(a.getMaxScore() < b.getMinScore() || b.getMaxScore() < a.getMinScore());
    }

    private void closeDialog() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}