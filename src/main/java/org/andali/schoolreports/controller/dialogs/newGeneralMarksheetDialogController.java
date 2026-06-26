package org.andali.schoolreports.controller.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;
import org.andali.schoolreports.service.SchoolClassService;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class newGeneralMarksheetDialogController implements Initializable {
    private final SchoolClassService schoolClassService;
    private final StageManager stageManager;
    @FXML
    public TextField generalMarksheetNameField;
    @FXML
    public ChoiceBox<SchoolClass> classBox;
    @FXML
    public ChoiceBox<ExamType> examBox;
    @FXML
    public ChoiceBox<Term> termBox;
    @FXML
    public Button cancelBtn;
    @FXML
    public Button okayBtn;
    @FXML
    public Label errorLabel;

    public newGeneralMarksheetDialogController(SchoolClassService schoolClassService, StageManager stageManager) {
        this.schoolClassService = schoolClassService;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupControls();

        cancelBtn.setOnAction(e -> {
            Stage stage = (Stage) termBox.getScene().getWindow();
            stage.close();
        });

        okayBtn.setOnAction(e -> {
            if (classBox.selectionModelProperty().getValue() != null &&
                    termBox.selectionModelProperty().getValue() != null &&
                    examBox.selectionModelProperty().getValue() != null &&
                    generalMarksheetNameField.getText() != null &&
                    !generalMarksheetNameField.getText().isBlank()
            ) {
                errorLabel.setVisible(false);
                Stage stage = (Stage) termBox.getScene().getWindow();
                stage.close();
                openCreatedGeneralMarksheet();
            } else {
                errorLabel.setText("Please fill in all the fields or cancel");
                errorLabel.setVisible(true);
            }

        });
    }

    private void setupControls() {
        var classes = schoolClassService.getAllSchoolClasses();

        classBox.getItems().addAll(classes);
        examBox.getItems().addAll(ExamType.values());
        termBox.getItems().addAll(Term.values());
        errorLabel.setVisible(false);
    }

    private void openCreatedGeneralMarksheet() {
        stageManager.clearContext();
        stageManager.setContextData("selectedClass", classBox.selectionModelProperty().getValue());
        stageManager.setContextData("selectedExam", examBox.selectionModelProperty().getValue());
        stageManager.setContextData("selectedterm", termBox.selectionModelProperty().getValue());
        stageManager.setContextData("newMarkshetName", generalMarksheetNameField.getText());
        stageManager.switchSchene("/view/marksheet/general-marksheet.fxml", "New Exam Marksheet");
    }
}
