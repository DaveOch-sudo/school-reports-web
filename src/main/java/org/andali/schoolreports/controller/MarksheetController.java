package org.andali.schoolreports.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.Setter;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.controller.dialogs.GradingSystemDialogController;
import org.andali.schoolreports.dto.MarksheetRowDTO;
import org.andali.schoolreports.model.*;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.MarksheetStatus;
import org.andali.schoolreports.model.enums.Term;
import org.andali.schoolreports.service.*;
import org.andali.schoolreports.utils.UnsavedChangesAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MarksheetController implements UnsavedChangesAware {

    private final StageManager stageManager;
    private final SchoolClassService schoolClassService;
    private final SchoolSubjectService schoolSubjectService;
    private final MarksheetService marksheetService;
    private final GradingScaleService gradingScaleService;
    private final StudentService studentService;

    // ── Top bar ─────────────────────────────────────────────────────────
    @FXML public ChoiceBox<SchoolClass> schoolClassChoice;
    @FXML public ChoiceBox<SchoolSubject> schoolSubjectChoice;
    @FXML public ChoiceBox<Term> termChoice;
    @FXML public ChoiceBox<ExamType> examChoice;
    @FXML public Button loadMarksheetBtn;
    @FXML public Button backBtn;

    // ── Middle bar ──────────────────────────────────────────────────────
    @FXML public Label marksheetNameLabel;
    @FXML public ChoiceBox<GradingScale> gradingSystemChoice;
    @FXML public Button newGradingSystemBtn;

    // ── Table ───────────────────────────────────────────────────────────
    @FXML public TableView<MarksheetRowDTO> marksheetTable;
    @FXML private TableColumn<MarksheetRowDTO, Long> idColumn;
    @FXML private TableColumn<MarksheetRowDTO, String> nameColumn;
    @FXML private TableColumn<MarksheetRowDTO, Integer> scoreColumn;
    @FXML private TableColumn<MarksheetRowDTO, String> gradeColumn;
    @FXML private TableColumn<MarksheetRowDTO, String> remarkColumn;

    // ── Summary panel ───────────────────────────────────────────────────
    @FXML public Label totalStudentsLabel;
    @FXML public Label highScoreLabel;
    @FXML public Label lowScoreLabel;
    @FXML public Label averagScoreLabel;

    // ── Bottom bar ──────────────────────────────────────────────────────
    @FXML public Button autoGradeBtn;
    @FXML public Button saveDraftBtn;
    @FXML public Button submitBtn;
    @FXML public Label missingMarkLabel;
    @FXML public Label totalMarkLabel;

    // ── Internal state ──────────────────────────────────────────────────
    private final ObservableList<MarksheetRowDTO> tableData = FXCollections.observableArrayList();
    private Marksheet currentMarksheet; // the loaded/existing marksheet, null if new
    private  final ConfigurableApplicationContext applicationContext;
    private boolean dirty = false;

    public MarksheetController(StageManager stageManager,
                               SchoolClassService schoolClassService,
                               SchoolSubjectService schoolSubjectService,
                               MarksheetService marksheetService,
                               GradingScaleService gradingScaleService, StudentService studentService, ConfigurableApplicationContext applicationContext) {
        this.stageManager = stageManager;
        this.schoolClassService = schoolClassService;
        this.schoolSubjectService = schoolSubjectService;
        this.marksheetService = marksheetService;
        this.gradingScaleService = gradingScaleService;
        this.studentService = studentService;
        this.applicationContext = applicationContext;
    }

    // ── Initialization ──────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupChoiceBoxes();
        setupTableColumns();
        makeScoreEditable();

        // Filter subjects when class selection changes
        schoolClassChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            schoolSubjectChoice.getItems().clear();
            schoolSubjectChoice.getItems().add(null);
            if (newVal != null) {
                schoolSubjectChoice.getItems().addAll(
                        schoolSubjectService.getAllBySchoolClass(newVal)
                );
            } else {
                schoolSubjectChoice.getItems().addAll(schoolSubjectService.getAllSubjects());
            }
            schoolSubjectChoice.setValue(null);
        });

        // Populate grading scales
        gradingSystemChoice.getItems().addAll(gradingScaleService.getAllGradingScales());
        gradingSystemChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(GradingScale gs) {
                return gs == null ? "Select..." : gs.getName();
            }
            @Override
            public GradingScale fromString(String s) { return null; }
        });

        // Re-grade live when grading scale is swapped
        gradingSystemChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !tableData.isEmpty()) {
                gradeAllRows(newVal);
                marksheetTable.refresh();
                markDirty();
                updateSummary();
            }
        });

        // Check if coming from "New Marksheet" flow
        SchoolClass contextClass = (SchoolClass) stageManager.getContextData("marksheet_class");
        SchoolSubject contextSubject = (SchoolSubject) stageManager.getContextData("marksheet_subject");
        ExamType contextExam = (ExamType) stageManager.getContextData("marksheet_exam");
        Term contextTerm = (Term) stageManager.getContextData("marksheet_term");

        Long preloadMarksheetId = (Long) stageManager.getContextData("marksheet_id");

        // check if the context has marksheet id not null
        if (preloadMarksheetId != null) {
            MarksheetStatus marksheetStatus = (MarksheetStatus) stageManager.getContextData("marksheet_status");
            currentMarksheet = marksheetService.getMarksheetById(preloadMarksheetId);

            if (currentMarksheet != null) {
                // preselect the choice boxes from the loaded marksheet
                schoolClassChoice.setValue(currentMarksheet.getSchoolClass());
                schoolSubjectChoice.setValue(currentMarksheet.getSchoolSubject());
                examChoice.setValue(currentMarksheet.getExamType());
                termChoice.setValue(currentMarksheet.getTerm());

                // disable editing for submitted marksheets
                if (marksheetStatus == MarksheetStatus.SUBMITTED) {
                    schoolClassChoice.setDisable(true);
                    schoolSubjectChoice.setDisable(true);
                    examChoice.setDisable(true);
                    termChoice.setDisable(true);
                    marksheetTable.setEditable(false);
                    gradingSystemChoice.setDisable(true);

                    loadMarksheetBtn.setDisable(true);
                    autoGradeBtn.setDisable(true);
                    saveDraftBtn.setDisable(true);
                    submitBtn.setDisable(true);
                    newGradingSystemBtn.setDisable(true);
                }

                handleLoadMarksheet(null);

            }
        }




        if (contextClass != null && contextSubject != null && contextExam != null && contextTerm != null) {
            schoolClassChoice.setValue(contextClass);
            schoolSubjectChoice.setValue(contextSubject);
            examChoice.setValue(contextExam);
            termChoice.setValue(contextTerm);


            // Clear context after use
            stageManager.clearContext();

            // Auto-trigger load
            handleLoadMarksheet(null);
        }
    }

    private void setupChoiceBoxes() {
        // School class
        schoolClassChoice.getItems().add(null);
        schoolClassChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolClass sc) { return sc == null ? "Select..." : sc.getClassName(); }
            @Override
            public SchoolClass fromString(String s) { return null; }
        });
        schoolClassChoice.getItems().addAll(schoolClassService.getAllSchoolClasses());

        // School subject
        schoolSubjectChoice.getItems().add(null);
        schoolSubjectChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolSubject ss) { return ss == null ? "Select..." : ss.getName(); }
            @Override
            public SchoolSubject fromString(String s) { return null; }
        });
        schoolSubjectChoice.getItems().addAll(schoolSubjectService.getAllSubjects());

        // Term
        termChoice.getItems().add(null);
        termChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Term t) { return t == null ? "Select..." : t.name(); }
            @Override
            public Term fromString(String s) { return null; }
        });
        termChoice.getItems().addAll(Term.values());

        // Exam type
        examChoice.getItems().add(null);
        examChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExamType et) { return et == null ? "Select..." : et.name(); }
            @Override
            public ExamType fromString(String s) { return null; }
        });
        examChoice.getItems().addAll(ExamType.values());

        gradingSystemChoice.setConverter(new StringConverter<>() {

            @Override
            public String toString(GradingScale gs) {
                return gs == null ? "Select..." : gs.getName();
            }

            @Override
            public GradingScale fromString(String s) {
                return null;
            }
        });
        gradingSystemChoice.getItems().addAll(gradingScaleService.getAllGradingScales());

    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        remarkColumn.setCellValueFactory(new PropertyValueFactory<>("remark"));
    }

    // ── Score editing ───────────────────────────────────────────────────

    private void makeScoreEditable() {
        marksheetTable.setEditable(true);

        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(
                new IntegerStringConverter()
        ));

        scoreColumn.setOnEditCommit(event -> {
            MarksheetRowDTO row = event.getRowValue();
            Integer newScore = event.getNewValue();
            markDirty();
            if (newScore == null) {
                // User cleared the field — treat as no score yet
                row.setScore(null);
                row.setGrade("-");
                row.setRemark("-");
            } else {
                row.setScore(newScore);
                // Re-grade this single row immediately
                if(gradingSystemChoice.getValue() != null){
                    GradingScale scale = gradingScaleService.getGradingScaleById(gradingSystemChoice.getValue().getId());
                    if (scale != null) {
                        resolveGradeForRow(row, scale);
                    }
                }
            }

            marksheetTable.refresh();
            updateSummary();
            updateMissingMarks();
        });
    }

    // ── Load marksheet ──────────────────────────────────────────────────

    @FXML
    public void handleLoadMarksheet(ActionEvent actionEvent) {
        SchoolClass selectedClass = schoolClassChoice.getValue();
        SchoolSubject selectedSubject = schoolSubjectChoice.getValue();
        Term selectedTerm = termChoice.getValue();
        ExamType selectedExam = examChoice.getValue();

        if (selectedClass == null || selectedSubject == null || selectedTerm == null || selectedExam == null) {
            showAlert("Validation", "Please select a class, subject, term, and exam type.");
            return;
        }

        // reload the full class
        SchoolClass fullClass = schoolClassService.getSchoolClassById(selectedClass.getId());
        List<Student> students = studentService.getAllBySchoolClass(fullClass);

        // Try to find an existing marksheet for this combination
        currentMarksheet = marksheetService.findByClassAndSubjectAndTermAndExam(
                fullClass.getId(), selectedSubject.getId(), selectedTerm, selectedExam
        );

        tableData.clear();

        if (currentMarksheet != null) {
            // Existing marksheet — populate table from saved StudentMarks
            for (StudentMark sm : currentMarksheet.getStudentMarks()) {
                MarksheetRowDTO row = new MarksheetRowDTO();
                row.setStudentId(sm.getStudent().getId());
                row.setStudentName(sm.getStudent().getName());
                row.setScore(sm.getScore() == 0 ? null : sm.getScore());
                row.setGrade(sm.getGrade() != null ? sm.getGrade() : "-");
                row.setRemark(sm.getRemark() != null ? sm.getRemark() : "-");
                System.out.println(sm.getStudent().getName());
                tableData.add(row);
            }
            // Restore the grading scale that was on the marksheet
            // Instead, find the matching scale from the choice box items (already fetched with steps)
            GradingScale loadedScale = gradingSystemChoice.getItems().stream()
                    .filter(gs -> gs != null && gs.getId().equals(currentMarksheet.getGradingScale().getId()))
                    .findFirst()
                    .orElse(null);
            gradingSystemChoice.setValue(loadedScale);
        } else {
            // New marksheet — populate table with all students in the class, scores empty
            for (Student student : students) {
                MarksheetRowDTO row = new MarksheetRowDTO();
                row.setStudentId(student.getId());
                row.setStudentName(student.getName());
                row.setScore(null);
                row.setGrade("-");
                row.setRemark("-");
                System.out.println(student.getName());
                tableData.add(row);
            }
        }

        marksheetTable.setItems(tableData);
        updateMarksheetNameLabel();
        updateSummary();
        updateMissingMarks();
    }

    // ── Auto grade all rows ─────────────────────────────────────────────

    @FXML
    public void handleGrading(ActionEvent actionEvent) {
        GradingScale scale = gradingScaleService.getGradingScaleById(gradingSystemChoice.getValue().getId());
        if (scale == null) {
            showAlert("Grading", "Please select a grading system first.");
            return;
        }
        gradeAllRows(scale);
        marksheetTable.refresh();
        updateSummary();
    }

    private void gradeAllRows(GradingScale scale) {
        for (MarksheetRowDTO row : tableData) {
            if (row.getScore() != null) {
                resolveGradeForRow(row, scale);
                markDirty();
            }
        }
    }

    private void resolveGradeForRow(MarksheetRowDTO row, GradingScale scale) {
        int score = row.getScore();

        GradeStep matched = scale.getSteps().stream()
                .filter(step -> score >= step.getMinScore() && score <= step.getMaxScore())
                .findFirst()
                .orElse(null);

        if (matched != null) {
            row.setGrade(matched.getGrade());
            row.setRemark(matched.getRemark() != null ? matched.getRemark() : "-");
        } else {
            row.setGrade("N/A");
            row.setRemark("Out of range");
        }
    }

    // ── Summary updates ─────────────────────────────────────────────────

    private void updateSummary() {
        List<Integer> scores = tableData.stream()
                .map(MarksheetRowDTO::getScore)
                .filter(s -> s != null)
                .collect(Collectors.toList());

        totalStudentsLabel.setText(String.valueOf(tableData.size()));

        if (scores.isEmpty()) {
            highScoreLabel.setText("0");
            lowScoreLabel.setText("0");
            averagScoreLabel.setText("0.0");
        } else {
            highScoreLabel.setText(String.valueOf(scores.stream().mapToInt(Integer::intValue).max().orElse(0)));
            lowScoreLabel.setText(String.valueOf(scores.stream().mapToInt(Integer::intValue).min().orElse(0)));
            averagScoreLabel.setText(String.format("%.1f", scores.stream().mapToInt(Integer::intValue).average().orElse(0.0)));
        }
    }

    private void updateMissingMarks() {
        long missing = tableData.stream()
                .filter(row -> row.getScore() == null)
                .count();
        missingMarkLabel.setText("Missing Marks: " + missing);
        missingMarkLabel.setTextFill(missing > 0
                ? javafx.scene.paint.Color.RED
                : javafx.scene.paint.Color.GREEN);
    }

    // ── Save as draft ───────────────────────────────────────────────────

    @FXML
    public void handleDrafting(ActionEvent actionEvent) {
        GradingScale scale = gradingSystemChoice.getValue();
        if (scale == null) {
            showAlert("Save Draft", "Please select a grading system before saving.");
            return;
        }

        Marksheet marksheet = buildMarksheetFromUI(scale, MarksheetStatus.DRAFT);

        // check if marksheet already exits and is not submitted
        if (currentMarksheet != null && currentMarksheet.getId() != null && !currentMarksheet.getStatus().equals(MarksheetStatus.SUBMITTED)) {
            marksheetService.updateMarksheet(currentMarksheet);
        } else {
            marksheetService.addNewMarksheet(marksheet);
        }

        currentMarksheet = marksheet;
        dirty = false;
        showAlert("Save Draft", "Draft saved successfully.");
    }

    // ── Submit marks ────────────────────────────────────────────────────

    @FXML
    public void handleSubmit(ActionEvent actionEvent) {
        GradingScale scale = gradingSystemChoice.getValue();
        if (scale == null) {
            showAlert("Submit", "Please select a grading system before submitting.");
            return;
        }

        long missing = tableData.stream().filter(row -> row.getScore() == null).count();
        if (missing > 0) {
            showAlert("Submit", "Cannot submit — " + missing + " student(s) still have missing marks.");
            return;
        }

        Marksheet marksheet = buildMarksheetFromUI(scale, MarksheetStatus.SUBMITTED);

        // check if marksheet already exists
        if (currentMarksheet != null && currentMarksheet.getId() != null) {
            marksheetService.updateMarksheet(currentMarksheet);
        } else {
            marksheetService.addNewMarksheet(marksheet);
        }
        currentMarksheet = marksheet;
        dirty = false;
        showAlert("Submit", "Marksheet submitted successfully.");
    }

    // ── Build marksheet from current UI state ──────────────────────────

    private Marksheet buildMarksheetFromUI(GradingScale scale, MarksheetStatus status) {

        Marksheet marksheet;
        if (currentMarksheet != null) {
            marksheet = currentMarksheet;
            marksheet.getStudentMarks().clear();
            marksheet.setUpdatedAt(LocalDateTime.now());
        } else {
            marksheet = new Marksheet();
            marksheet.setCreatedAt(LocalDateTime.now());
            marksheet.setUpdatedAt(LocalDateTime.now());
        }

        List<StudentMark> studentMarks = new ArrayList<>();

        for (MarksheetRowDTO row : tableData) {
            StudentMark sm = new StudentMark();
            Student student = studentService.getStudentById(row.getStudentId());

            sm.setStudent(student);
            sm.setScore(row.getScore() != null ? row.getScore() : 0);

            sm.setMarksheet(marksheet);
            studentMarks.add(sm);
        }

        marksheet.setStudentMarks(studentMarks);

        marksheet.setName(
                schoolClassChoice.getValue().getClassName() + "-" +
                        schoolSubjectChoice.getValue().getName() + "-" +
                        termChoice.getValue() + "-" +
                        examChoice.getValue()
        );

        marksheet.setSchoolClass(schoolClassChoice.getValue());
        marksheet.setSchoolSubject(schoolSubjectChoice.getValue());
        marksheet.setTerm(termChoice.getValue());
        marksheet.setExamType(examChoice.getValue());
        marksheet.setGradingScale(scale);
        marksheet.setStatus(status);

        return marksheet;
    }

    // ── UI helpers ──────────────────────────────────────────────────────

    private void updateMarksheetNameLabel() {
        SchoolClass sc = schoolClassChoice.getValue();
        SchoolSubject ss = schoolSubjectChoice.getValue();
        Term t = termChoice.getValue();
        ExamType e = examChoice.getValue();

        String label = (sc != null ? sc.getClassName() : "?") + " - "
                + (ss != null ? ss.getName() : "?") + " - "
                + (t != null ? t.name() : "?") + " - "
                + (e != null ? e.name() : "?");

        marksheetNameLabel.setText(label);
    }

    @FXML
    public void previousScene(ActionEvent actionEvent) {
        stageManager.loadView("/view/marksheet/marksheets.fxml", "Manage Marksheets");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add this method to your MarksheetController class

    @FXML
    public void handleNewGradingSystem(ActionEvent actionEvent) {
        try {
            // Load the dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/grading-system-dialog.fxml"));

            // Set the controller factory to use Spring's context
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Grading System");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(newGradingSystemBtn.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            // Show and wait for the dialog to close
            dialogStage.showAndWait();

            // After dialog closes, check if a grading scale was saved
            GradingSystemDialogController controller = loader.getController();
            GradingScale savedScale = controller.getSavedGradingScale();

            if (savedScale != null) {
                // Refresh the grading system choice box
                gradingSystemChoice.getItems().clear();
                gradingSystemChoice.getItems().addAll(gradingScaleService.getAllGradingScales());

                // Auto-select the newly created grading scale
                gradingSystemChoice.setValue(savedScale);
                markDirty();
                showAlert("Success", "Grading system created successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open grading system dialog: " + e.getMessage());
        }
    }

    @Override
    public boolean hasUnsavedChanges() {
        return dirty;
    }

    @Override
    public String getUnsavedChangesMessage() {
        return "Marksheet has to be saved as Draft or Submitted";
    }

    @Override
    public void saveChanges() {
        handleDrafting(null);
    }

    public  void markDirty(){
        dirty = true;
        String title = stageManager.getTitle();
        stageManager.updateTitle("* "+title);
    }
}