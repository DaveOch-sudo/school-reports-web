package org.andali.schoolreports.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.andali.schoolreports.config.StageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WelcomeController {
    private final StageManager stageManager;

    @FXML
    public Button welcomeBtn;

    @Autowired
    private ApplicationContext applicationContext;

    public WelcomeController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void loadDashboard() {

        MainController mainController = stageManager.switchSchene("/view/layouts/MainLayout.fxml", "");

        stageManager.setMainController(mainController);

        stageManager.loadView("/view/Dashboard.fxml", "Dashboard");
    }
}
