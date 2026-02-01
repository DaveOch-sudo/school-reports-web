package org.andali.schoolreports.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.andali.schoolreports.config.StageManager;
import org.springframework.stereotype.Controller;

@Controller
public class DashboardController {

    @FXML
    public Button subjectsBtn;
    @FXML
    public Button settingsBtn;
    @FXML
    public Button classesBtn;
    @FXML
    public Button reportsBtn;
    @FXML
    public Button gradeBtn;
    @FXML
    public Button studentsBtn;
    @FXML
    public Label termLabel;
    @FXML
    public Label titleLabel;

    private final StageManager stageManager;

    public DashboardController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    public void loadStudentsScene() {
        stageManager.switchSchene("/view/students.fxml",  "Manage Students");
    }

    public void loadMarksheetScene() {
        stageManager.switchSchene("/view/marksheet/marksheets.fxml",  "Manage Marksheets");
    }

    public void loadReportsScene() {
    }

    public void loadClassesScene() {
        stageManager.switchSchene("/view/classes.fxml",  "Manage Classes");
    }

    public void loadSubjectScene() {
        stageManager.switchSchene("/view/subjects.fxml",  "Manage Subjects");
    }

    public void loadSettingsScene() {
    }
}
