package org.andali.schoolreports.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.andali.schoolreports.config.StageManager;
import org.springframework.stereotype.Component;

@Component
public class WelcomeController {
    private final StageManager stageManager;

    @FXML
    public Button welcomeBtn;

    public WelcomeController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void loadDashboard() {
         stageManager.switchSchene("/view/Dashboard.fxml", "Dashboard");
    }
}
