package org.andali.schoolreportsweb;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SchoolReportsApplication {
    @Getter
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Start Spring context
        context = SpringApplication.run(SchoolReportsApplication.class, args);

    }

}