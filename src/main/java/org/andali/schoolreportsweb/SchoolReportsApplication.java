package org.andali.schoolreportsweb;

import lombok.Getter;
import org.andali.schoolreportsweb.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SchoolReportsApplication {
    @Getter
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Start Spring context
        context = SpringApplication.run(SchoolReportsApplication.class, args);

    }

}