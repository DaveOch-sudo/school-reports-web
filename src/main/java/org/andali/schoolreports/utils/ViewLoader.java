package org.andali.schoolreports.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class ViewLoader {
    @Setter
    private static StackPane mainPane;

    public void load(String fxml) {
        try {
            Parent view = FXMLLoader.load(
                    ViewLoader.class.getResource(fxml)
            );

            mainPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
