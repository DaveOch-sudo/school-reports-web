package org.andali.schoolreports;

import javafx.application.Application;
import lombok.Getter;
import org.andali.schoolreports.application.JavaFxApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.andali.schoolreports")
public class SchoolReportsApplication {
    @Getter
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Start Spring context
        context = SpringApplication.run(SchoolReportsApplication.class, args);

        // Launch JavaFX app
        Application.launch(JavaFxApplication.class, args);
    }

}
