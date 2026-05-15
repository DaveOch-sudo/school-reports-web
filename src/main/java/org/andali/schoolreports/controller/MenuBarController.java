package org.andali.schoolreports.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.andali.schoolreports.application.JavaFxApplication;
import org.springframework.stereotype.Controller;

@Controller
public class MenuBarController {

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
    }

    @FXML
    public void handleViewSubjects(ActionEvent actionEvent) {
    }

    @FXML
    public void handleMarksheetsView(ActionEvent actionEvent) {
    }

    @FXML
    public void handleGeneralMarksheetsView(ActionEvent actionEvent) {
    }

    @FXML
    public void handleAbout(ActionEvent actionEvent) {
    }
}
