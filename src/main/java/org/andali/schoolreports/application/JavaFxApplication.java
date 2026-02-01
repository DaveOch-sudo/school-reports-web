package org.andali.schoolreports.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.andali.schoolreports.SchoolReportsApplication;
import org.andali.schoolreports.event.StageReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = SchoolReportsApplication.getContext();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));

    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }


}
