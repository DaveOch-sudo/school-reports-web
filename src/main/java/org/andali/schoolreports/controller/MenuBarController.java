package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.andali.schoolreports.application.JavaFxApplication;
import org.andali.schoolreports.config.StageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MenuBarController {
    @Autowired
    StageManager stageManager;

    @FXML
    public MenuItem saveItem;
    @FXML
    public MenuItem exportItem;
    @FXML
    public MenuItem exitItem;
    @FXML
    public MenuItem copyItem;
    @FXML
    public MenuItem pasteItem;
    @FXML
    public MenuItem cutItem;
    @FXML
    public MenuItem studentsItem;
    @FXML
    public MenuItem subjectsItem;
    @FXML
    public MenuItem marksheetsItem;
    @FXML
    public MenuItem generalMarksheetsItem;
    @FXML
    public MenuItem aboutItem;

    @FXML
    public void handleSave(ActionEvent actionEvent) {
    }

    @FXML
    public void handleExport(ActionEvent actionEvent) {
    }

    @FXML
    public void handleExit(ActionEvent actionEvent) {


    }

    @FXML
    public void handleCopy(ActionEvent actionEvent) {
    }

    @FXML
    public void handlePaste(ActionEvent actionEvent) {
    }

    @FXML
    public void handleCut(ActionEvent actionEvent) {
    }

    @FXML
    public void handleViewStudents(ActionEvent actionEvent) {
        stageManager.loadView("/view/students.fxml", "Students");
    }

    @FXML
    public void handleViewSubjects(ActionEvent actionEvent) {
        stageManager.loadView("/view/subjects.fxml", "Subjects");
    }

    @FXML
    public void handleMarksheetsView(ActionEvent actionEvent) {
        stageManager.loadView("/view/marksheet/marksheets.fxml", "Subject Marksheets");
    }

    @FXML
    public void handleGeneralMarksheetsView(ActionEvent actionEvent) {
        stageManager.loadView("/view/marksheet/general-marksheet-landing.fxml", "General Marksheets");
    }

    @FXML
    public void handleAbout(ActionEvent actionEvent) {
    }
}
