package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.controller.dialogs.MarksheetSetterDialogController;
import org.andali.schoolreports.dto.GeneralMarksheetDashboardDTO;
import org.andali.schoolreports.dto.GeneralMarksheetSummaryDTO;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.MarksheetStatus;
import org.andali.schoolreports.model.enums.Term;
import org.andali.schoolreports.service.GeneralMarksheetService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

@Controller
public class GeneralMarksheetSummaryController implements Initializable {
    private final GeneralMarksheetService generalMarksheetService;
    private final StageManager stageManager;
    private final ConfigurableApplicationContext applicationContext;
    @FXML
    public Label totalMarksheetsLabel;
    @FXML
    public Label classesCoveredLabel;
    @FXML
    public Label examsCoveredLabel;
    @FXML
    public Label lastGeneratedLabel;
    @FXML
    public Button generateButton;
    @FXML
    public TableView<GeneralMarksheetSummaryDTO> marksheetTable;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, String> classColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, Term> termColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, ExamType> examColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, Integer> subjectsCountColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, Integer> studentsCountColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, Double> averageColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, MarksheetStatus> statusColumn;
    @FXML
    public TableColumn<GeneralMarksheetSummaryDTO, LocalDateTime> generatedOnColumn;
    @FXML
    public ProgressIndicator tableProgressIndicator;
    @FXML
    public Button viewButton;

    public GeneralMarksheetSummaryController(GeneralMarksheetService generalMarksheetService, StageManager stageManager, ConfigurableApplicationContext applicationContext) {
        this.generalMarksheetService = generalMarksheetService;
        this.stageManager = stageManager;
        this.applicationContext = applicationContext;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        showLoadingState();

        Platform.runLater(this::loadDataAsync);

        viewButton.setOnAction(e -> {
            GeneralMarksheetSummaryDTO selected = getSelectedMarksheet();

            if (selected == null) {
                showWarning("Please select a marksheet to view");
                return;
            }
            stageManager.setContextData("selectedMarksheet", selected.getId());
            stageManager.switchSchene("/view/marksheet/general-marksheet.fxml", "Exam Marksheets");
        });

        // open a modal window with a form to capture new marksheet details
        generateButton.setOnAction(event -> {
            try {
                // Load the dialog FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/new-general-marksheet-dialog.fxml"));
                loader.setControllerFactory(applicationContext::getBean);

                Parent root = loader.load();

                // Create dialog stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("New Marksheet Details");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(generateButton.getScene().getWindow());

                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                dialogStage.setResizable(false);

                // Show and wait
                dialogStage.showAndWait();

                // Check if user confirmed
//                MarksheetSetterDialogController controller = loader.getController();
//                if (controller.isConfirmed()) {
//                    // Navigate to marksheet form and pass the selected values
//                    // Store selections in context
//                    stageManager.setContextData("marksheet_class", controller.getSelectedClass());
//                    stageManager.setContextData("marksheet_subject", controller.getSelectedSubject());
//                    stageManager.setContextData("marksheet_exam", controller.getSelectedExam());
//                    stageManager.setContextData("marksheet_term", controller.getSelectedTerm());
//
//                    // You'll need to modify your marksheet form to accept these parameters
//                    stageManager.loadView("/view/marksheet/marksheetForm.fxml", "New Marksheet");
//
//                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to open dialog: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                var stats = generalMarksheetService.getDashboardStats();
                var rows = generalMarksheetService.getLandingRows();

                Platform.runLater(() ->{
                    updateDashboard(stats);
                    marksheetTable.getItems().setAll(rows);
                    hideLoadingState();
                });
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateDashboard(GeneralMarksheetDashboardDTO stats) {
        totalMarksheetsLabel.setText(stats.getTotalMarksheets().toString());
        classesCoveredLabel.setText(stats.getClassesCovered().toString());
        examsCoveredLabel.setText(stats.getExamsCovered().toString());

        if (stats.getLastGenerated() != null){
            lastGeneratedLabel.setText(stats.getLastGenerated().toString());
        }

    }

    private void hideLoadingState() {
        tableProgressIndicator.setVisible(false);
        marksheetTable.setDisable(false);
    }

    private void setupTableColumns() {

        marksheetTable.setPlaceholder(
                new Label("No general marksheets available")
        );

        classColumn.setCellValueFactory(
                new PropertyValueFactory<>("className")
        );

        termColumn.setCellValueFactory(
                new PropertyValueFactory<>("term")
        );

        examColumn.setCellValueFactory(
                new PropertyValueFactory<>("examType")
        );

        studentsCountColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentCount")
        );

        subjectsCountColumn.setCellValueFactory(
                new PropertyValueFactory<>("subjectsCount")
        );

        averageColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        generatedOnColumn.setCellFactory(
                column -> new TableCell<>(){
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty){
                        super.updateItem(item, empty);

                        if(empty|| item == null){
                            setText("-");
                        } else {
                            setText(item.toLocalDate().toString());
                        }
                    }
                }
        );
    }

    private void showLoadingState() {
        tableProgressIndicator.setVisible(true);
        marksheetTable.setDisable(true);

        totalMarksheetsLabel.setText("...");
        classesCoveredLabel.setText("...");
        examsCoveredLabel.setText("...");
        lastGeneratedLabel.setText("...");
    }

    private GeneralMarksheetSummaryDTO getSelectedMarksheet() {
        return marksheetTable.getSelectionModel().getSelectedItem();
    }
}
