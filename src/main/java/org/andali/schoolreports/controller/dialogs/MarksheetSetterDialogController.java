package org.andali.schoolreports.controller.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.SchoolSubject;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;
import org.andali.schoolreports.service.SchoolClassService;
import org.andali.schoolreports.service.SchoolSubjectService;
import org.springframework.stereotype.Controller;

@Controller
public class MarksheetSetterDialogController {

    @FXML public ChoiceBox<SchoolClass> classBox;
    @FXML public ChoiceBox<SchoolSubject> subjectBox;
    @FXML public ChoiceBox<ExamType> examBox;
    @FXML public ChoiceBox<Term> termBox;
    @FXML public Button cancelBtn;
    @FXML public Button okayBtn;

    private final SchoolClassService schoolClassService;
    private final SchoolSubjectService schoolSubjectService;

    private boolean confirmed = false;
    private SchoolClass selectedClass;
    private SchoolSubject selectedSubject;
    private ExamType selectedExam;
    private Term selectedTerm;

    public MarksheetSetterDialogController(SchoolClassService schoolClassService,
                                           SchoolSubjectService schoolSubjectService) {
        this.schoolClassService = schoolClassService;
        this.schoolSubjectService = schoolSubjectService;
    }

    @FXML
    public void initialize() {
        setupChoiceBoxes();
        setupButtons();

        // Filter subjects when class changes
        classBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            subjectBox.getItems().clear();
            if (newVal != null) {
                subjectBox.getItems().addAll(
                        schoolSubjectService.getAllBySchoolClass(newVal)
                );
            }
        });
    }

    private void setupChoiceBoxes() {
        // Class choice box
        classBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolClass sc) {
                return sc == null ? "Select..." : sc.getClassName();
            }
            @Override
            public SchoolClass fromString(String s) { return null; }
        });
        classBox.getItems().addAll(schoolClassService.getAllSchoolClasses());

        // Subject choice box
        subjectBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolSubject ss) {
                return ss == null ? "Select..." : ss.getName();
            }
            @Override
            public SchoolSubject fromString(String s) { return null; }
        });

        // Exam choice box
        examBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExamType et) {
                return et == null ? "Select..." : et.name();
            }
            @Override
            public ExamType fromString(String s) { return null; }
        });
        examBox.getItems().addAll(ExamType.values());

        // Term choice box
        termBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Term t) {
                return t == null ? "Select..." : t.name();
            }
            @Override
            public Term fromString(String s) { return null; }
        });
        termBox.getItems().addAll(Term.values());
    }

    private void setupButtons() {
        okayBtn.setOnAction(event -> {
            selectedClass = classBox.getValue();
            selectedSubject = subjectBox.getValue();
            selectedExam = examBox.getValue();
            selectedTerm = termBox.getValue();

            if (selectedClass == null || selectedSubject == null ||
                    selectedExam == null || selectedTerm == null) {
                // Could show an alert here, but for now just prevent closing
                return;
            }

            confirmed = true;
            closeDialog();
        });

        cancelBtn.setOnAction(event -> {
            confirmed = false;
            closeDialog();
        });
    }

    private void closeDialog() {
        Stage stage = (Stage) okayBtn.getScene().getWindow();
        stage.close();
    }

    // Getters for the parent controller to access selected values
    public boolean isConfirmed() {
        return confirmed;
    }

    public SchoolClass getSelectedClass() {
        return selectedClass;
    }

    public SchoolSubject getSelectedSubject() {
        return selectedSubject;
    }

    public ExamType getSelectedExam() {
        return selectedExam;
    }

    public Term getSelectedTerm() {
        return selectedTerm;
    }
}