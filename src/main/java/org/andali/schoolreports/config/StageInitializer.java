package org.andali.schoolreports.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.NonNull;
import org.andali.schoolreports.event.StageReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Autowired
    private StageManager stageManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(@NonNull StageReadyEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/welcome.fxml")
            );

            loader.setControllerFactory(applicationContext::getBean);

            Parent initRoot = loader.load();

            // store it inside the stage manager
            stageManager.setPrimaryStage(event.getStage());


            Scene scene = new Scene(initRoot, 1000, 700);
            event.getStage().setScene(scene);

            // intercept window close
            event.getStage().setOnCloseRequest(e -> {
                boolean canClose = stageManager.confirmCloseCurrentView();

                if (!canClose) {
                    e.consume(); // prevent closing the app
                }
            });

            event.getStage().setTitle("School Reports");
            event.getStage().show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
