package org.andali.schoolreports.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Setter;
import org.andali.schoolreports.controller.MainController;
import org.andali.schoolreports.utils.UnsavedChangesAware;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class StageManager {
    private Stage stage;
    private Scene scene;
    private Map<String, Object> sceneContext = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Setter
    private Parent rootLayout;
    @Setter
    private MainController mainController;

    private Object currentController;


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

    // switches entire scene
    public <T> T switchSchene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // check current view for unsaved changes
            if (!canLeaveCurrentView()) {
                return null; // cancel navigation
            }
            stage.getScene().setRoot(root);
            stage.setTitle(title);
            stage.show();

            // set the new controller as current when switching
            currentController = loader.getController();

            return (T) currentController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    // switches part of the scene leaving the menubar constant
    public <T> T loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // check for possible unsaved changes
            if (!canLeaveCurrentView()) {
                return null; // cancel navigation
            }

            mainController.getContentPane().getChildren().setAll(view);

            stage.setTitle(title);

            // set the new controller as current when switching
            currentController = loader.getController();

            return (T) currentController;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public boolean canLeaveCurrentView() {

        if (currentController instanceof UnsavedChangesAware) {

            UnsavedChangesAware aware =
                    (UnsavedChangesAware) currentController;

            if (aware.hasUnsavedChanges()) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Unsaved Changes");
                alert.setHeaderText("You have unsaved work");

                alert.setContentText(
                        aware.getUnsavedChangesMessage() +
                                "\n\nDo you want to leave without saving?"
                );

                Optional<ButtonType> result = alert.showAndWait();

                return result.isPresent() && result.get() == ButtonType.OK;
            }
        }

        return true;
    }

    public boolean confirmCloseCurrentView() {
        if (currentController instanceof UnsavedChangesAware aware) {
            if (aware.hasUnsavedChanges()) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Unsaved Changes");
                alert.setHeaderText("You have unsaved changes.");
                alert.setContentText("Do you want to discard them and continue?");

                ButtonType save = new ButtonType("Save");
                ButtonType discard = new ButtonType("Discard");
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(save, discard, cancel);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isEmpty() || result.get() == cancel) {
                    return false; // stop action
                }

                if (result.get() == save) {
                    aware.saveChanges();
                }
            }
        }
        return true; // safe to proceed
    }

    public <T> T openNewStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(applicationContext::getBean);
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // check for possible unsaved changes
            if (!canLeaveCurrentView()) {
                return null; // cancel navigation
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // set the new controller as current when switching
            currentController = loader.getController();

            return (T) currentController;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTitle() {
        return stage.getTitle();
    }

    public void updateTitle(String title) {
        stage.setTitle(title);
    }
}
