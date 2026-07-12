package org.andali.schoolreportsweb.util;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.subject.SubjectsSeederService;
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
