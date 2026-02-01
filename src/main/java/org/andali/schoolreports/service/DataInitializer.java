package org.andali.schoolreports.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubjectsSeederService subjectSeederService;

    @Override
    public void run(String... args) {
        subjectSeederService.seedSubjects();
    }
}
