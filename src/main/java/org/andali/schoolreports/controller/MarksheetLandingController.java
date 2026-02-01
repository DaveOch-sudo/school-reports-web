package org.andali.schoolreports.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.controller.dialogs.MarksheetSetterDialogController;
import org.andali.schoolreports.dto.MarksheetsLandingDto;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.SchoolSubject;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.MarksheetStatus;
import org.andali.schoolreports.model.enums.Term;
import org.andali.schoolreports.service.MarksheetService;
import org.andali.schoolreports.service.SchoolClassService;
import org.andali.schoolreports.service.SchoolSubjectService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class MarksheetLandingController implements Initializable {
    @FXML
    public Label draftsLabel;
    @FXML
    public Label submittedLabel;
    @FXML
    public Label totalMarksheetsLabel;
    @FXML
    public TableView<MarksheetsLandingDto> marksheetListTable;
    @FXML
    public TableColumn<MarksheetsLandingDto, Void> actionColumn;
    @FXML
    public TableColumn<MarksheetsLandingDto, LocalDateTime> lastUpdateColumn;
    @FXML
    public TableColumn<MarksheetsLandingDto, MarksheetStatus> marksheetStatusColumn;
    @FXML
    public TableColumn<MarksheetsLandingDto, ExamType> examTypeColumn;
    @FXML
    public TableColumn<MarksheetsLandingDto, Term> termColumn;
    @FXML
    public TableColumn<MarksheetsLandingDto, SchoolClass> schoolClassColumn;
    @FXML
    public TableColumn<MarksheetsLandingDto, SchoolSubject> subjectColumn;
    @FXML
    public Button backBtn;
    @FXML
    public Button newMarksheetBtn;
    @FXML
    public ChoiceBox<SchoolSubject> schoolSubjectChoice;
    @FXML
    public ChoiceBox<ExamType> examTypeChoice;
    @FXML
    public ChoiceBox<Term> termChoice;
    @FXML
    public ChoiceBox<SchoolClass> schoolClassChoice;
    @FXML
    public ChoiceBox<Integer> yearChoice;

    private final StageManager stageManager;
    private final MarksheetService marksheetService;
    private final SchoolClassService schoolClassService;
    private final SchoolSubjectService schoolSubjectService;
    private final ConfigurableApplicationContext applicationContext;
    private List<MarksheetsLandingDto> dtos = new ArrayList<>();

    public MarksheetLandingController(StageManager stageManager, MarksheetService marksheetService, SchoolClassService schoolClassService, SchoolSubjectService schoolSubjectService, ConfigurableApplicationContext applicationContext) {
        this.stageManager = stageManager;
        this.marksheetService = marksheetService;
        this.schoolClassService = schoolClassService;
        this.schoolSubjectService = schoolSubjectService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize the marksheet list
        dtos.clear();
        dtos.addAll(marksheetService.allMarksheetsToDto());
        marksheetListTable.setItems(FXCollections.observableList(dtos));

        // populate the choice boxes
        // school class
        schoolClassChoice.getItems().add(null);
        schoolClassChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolClass sc) {
                return sc == null ? "All" : sc.getClassName();
            }

            @Override
            public SchoolClass fromString(String s) {
                return null;
            }
        });
        schoolClassChoice.getItems().addAll(schoolClassService.getAllSchoolClasses());

        // school subject
        schoolSubjectChoice.getItems().add(null);
        schoolSubjectChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(SchoolSubject ss) { return ss == null ? "All" : ss.getName(); }

            @Override
            public SchoolSubject fromString(String s) {
                return null;
            }
        });
        schoolSubjectChoice.getItems().addAll(schoolSubjectService.getAllSubjects());

        // exam types
        examTypeChoice.getItems().add(null);
        examTypeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExamType et) { return et == null ? "All" : et.name(); }

            @Override
            public ExamType fromString(String s) {
                return null;
            }
        });
        examTypeChoice.getItems().addAll(ExamType.values());

        // terms
        termChoice.getItems().add(null);
        termChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Term t) { return t == null ? "All" : t.name(); }

            @Override
            public Term fromString(String s) {
                return null;
            }
        });
        termChoice.getItems().addAll(Term.values());

        // the year drop down
        int firstYear = LocalDate.now().getYear() - 5;
        for(int i = 1; i <=10; i++){
            yearChoice.getItems().add(firstYear);
             firstYear ++;
        }
        // set the table columns
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("schoolSubject"));
        schoolClassColumn.setCellValueFactory(new PropertyValueFactory<>("schoolClass"));
        termColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("examType"));
        marksheetStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        lastUpdateColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));
        
        lastUpdateColumn.setCellFactory(col -> new TableCell<MarksheetsLandingDto, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                
                if (empty || dateTime == null) {
                    setText(null);
                    return;
                }
                
                setText(formatRelativeTime(dateTime));
                setStyle("-fx-text-fill: #777;");
            }
        });
        
        actionColumn.setCellFactory(col -> new TableCell<MarksheetsLandingDto, Void>() {

            private final Button actionBtn = new Button();

            {
                actionBtn.setOnAction(event -> {
                    MarksheetsLandingDto dto = getTableView()
                            .getItems()
                            .get(getIndex());

                    if (dto.getStatus() == MarksheetStatus.SUBMITTED) {
                        viewMarksheet(dto.getMarksheetId());
                    } else {
                        loadSelectedMarksheet(dto.getMarksheetId());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                MarksheetsLandingDto dto = getTableView()
                        .getItems()
                        .get(getIndex());

                if (dto.getStatus() == MarksheetStatus.SUBMITTED) {
                    actionBtn.setText("View");
                    actionBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                } else {
                    actionBtn.setText("Open");
                    actionBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                }

                setGraphic(actionBtn);
            }
        });


        // add change listener to year
        yearChoice.selectionModelProperty().addListener((observable, oldValue, newValue) -> {

        });



        newMarksheetBtn.setOnAction(event -> {
            try {
                // Load the dialog FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/marksheet-setter-dialog.fxml"));
                loader.setControllerFactory(applicationContext::getBean);

                Parent root = loader.load();

                // Create dialog stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("New Marksheet Details");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(newMarksheetBtn.getScene().getWindow());

                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                dialogStage.setResizable(false);

                // Show and wait
                dialogStage.showAndWait();

                // Check if user confirmed
                MarksheetSetterDialogController controller = loader.getController();
                if (controller.isConfirmed()) {
                    // Navigate to marksheet form and pass the selected values
                    // Store selections in context
                    stageManager.setContextData("marksheet_class", controller.getSelectedClass());
                    stageManager.setContextData("marksheet_subject", controller.getSelectedSubject());
                    stageManager.setContextData("marksheet_exam", controller.getSelectedExam());
                    stageManager.setContextData("marksheet_term", controller.getSelectedTerm());

                    // You'll need to modify your marksheet form to accept these parameters
                    stageManager.switchSchene("/view/marksheet/marksheetForm.fxml", "New Marksheet");

                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to open dialog: " + e.getMessage());
                alert.showAndWait();
            }
        });

        backBtn.setOnAction(event -> {
            stageManager.switchSchene("/view/Dashboard.fxml", "Dashboard");
        });

        updateOverviewLabels();
    }

    private String formatRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now  = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 1 ) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        if (hours < 24) return hours + " hours ago";
        if (days == 1 ) return "Yesterday";

        return days + " days ago";

    }

    private void viewMarksheet(Long marksheetId) {
        stageManager.setContextData("marksheet_id", marksheetId);
        stageManager.setContextData("marksheet_status", MarksheetStatus.SUBMITTED);
        stageManager.switchSchene("/view/marksheet/marksheetForm.fxml", "Marksheet");
    }

    private void loadSelectedMarksheet(Long marksheetId) {
        stageManager.setContextData("marksheet_id", marksheetId);
        stageManager.setContextData("marksheet_status", MarksheetStatus.DRAFT);
        stageManager.switchSchene("/view/marksheet/marksheetForm.fxml", "Marksheet");
    }

    public void populateUI() {
        marksheetListTable.setItems(FXCollections.observableList(marksheetService.allMarksheetsToDto()));

    }

    private void updateOverviewLabels() {
        totalMarksheetsLabel.setText(String.valueOf(dtos.size()));
        long drafts = dtos.stream().filter(d -> d.getStatus() == MarksheetStatus.DRAFT).count();
        long submitted = dtos.stream().filter(d -> d.getStatus() == MarksheetStatus.SUBMITTED).count();
        draftsLabel.setText(String.valueOf(drafts));
        submittedLabel.setText(String.valueOf(submitted));
    }


}
