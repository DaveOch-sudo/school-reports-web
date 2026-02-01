package org.andali.schoolreports.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class StageManager {
    private Stage stage;
    private Scene scene;
    private Map<String, Object> sceneContext = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void setPrimaryScene(Scene scene) {
        this.scene = scene;
        stage.setScene(this.scene);
    }

    public void setContextData(String key, Object value) {
        sceneContext.put(key, value);
    }

    public Object getContextData(String key) {
        return sceneContext.get(key);
    }

    public void clearContext() {
        sceneContext.clear();
    }

    public <T> T switchSchene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            stage.getScene().setRoot(root);
            stage.setTitle(title);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void openNewStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
