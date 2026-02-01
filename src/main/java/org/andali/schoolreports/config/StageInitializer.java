package org.andali.schoolreports.config;

import javafx.scene.Parent;
import javafx.scene.Scene;
import org.andali.schoolreports.event.StageReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Autowired
    private StageManager stageManager;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        System.out.println("StageReadyEvent fired!");
        stageManager.setPrimaryStage(event.getStage());
        stageManager.setPrimaryScene(new Scene(new Parent() {
        }, 1000, 700));
        event.getStage().show();
        stageManager.switchSchene("/view/welcome.fxml", "Welcome");
        System.out.println("Stage: " + event.getStage());
    }
}
