package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import org.andali.schoolreports.config.StageManager;
import org.andali.schoolreports.event.ClassSavedEvent;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.service.SchoolClassService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ClassesController implements Initializable {

    private final StageManager stageManager;
    @FXML
    public TextField searchField;
    @FXML
    public TableView<SchoolClass> classesTable;
    @FXML
    public TableColumn<SchoolClass, String> idColumn;
    @FXML
    public TableColumn<SchoolClass, String> nameColumn;
    @FXML
    public TextField newClass;
    @FXML
    public Button addClassBtn;
    @FXML
    public Button searchBtn;
    @FXML
    public Button previousSceneBtn;

    private final SchoolClassService schoolClassService;


    public ClassesController(SchoolClassService schoolClassService, StageManager stageManager) {
        this.schoolClassService = schoolClassService;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reloadClasses();
        System.out.println("ClassesController initialize successfully");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));

        classesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        classesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                SchoolClass selected = classesTable.getSelectionModel().getSelectedItem();
                System.out.println("Selected class: " + selected);
                if (selected != null) {
                   ClassDetailController controller =
                           stageManager.loadView(
                                   "/view/classDetail.fxml",
                                   "Class Details");

                    // pass the selected school class to the new scene controller
                    controller.setSelectedClass(selected);

                }
            }
        });

        addClassBtn.setOnAction(event -> {
           String name = newClass.getText();
           SchoolClass schoolClass;

           if (!name.isEmpty()) {

               schoolClass = new SchoolClass(null, name,null,null);
               schoolClassService.AddSchoolClass(schoolClass);
               newClass.setText("");

               Alert saveAlert = new Alert(Alert.AlertType.INFORMATION);
               saveAlert.setTitle("Class Saved");
               saveAlert.setHeaderText("Class Saved");
               saveAlert.setContentText("School class saved successfully");
               saveAlert.show();
               reloadClasses();

           } else {
               System.out.println("Name is empty");
           }

        });

        previousSceneBtn.setOnAction(event ->
                stageManager.loadView("/view/Dashboard.fxml","Dashboard"));
    }

    public void reloadClasses(){
        classesTable.getItems().setAll(schoolClassService.getAllSchoolClasses());
    }

//    @EventListener
//    public void onSchoolClassSave(ClassSavedEvent event) {
//        Platform.runLater(this::reloadClasses);
//    }

}
