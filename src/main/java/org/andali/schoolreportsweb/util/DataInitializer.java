package org.andali.schoolreportsweb.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.andali.schoolreportsweb.enums.UserRole;
import org.andali.schoolreportsweb.subject.SubjectsSeederService;
import org.andali.schoolreportsweb.user.User;
import org.andali.schoolreportsweb.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubjectsSeederService subjectSeederService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {
            User superAdmin = new User();

            superAdmin.setName("System Administrator");
            superAdmin.setEmail("admin@schoolreports.com");
            superAdmin.setPhone("100200300400");
            superAdmin.setPasswordHash(
                    passwordEncoder.encode("admin@Schoolreports")
            );

            superAdmin.setRole(UserRole.SUPER_ADMIN);
            userRepository.save(superAdmin);

            System.out.println("Initial SUPER_ADMIN created");
        }
        subjectSeederService.seedSubjects();
    }
}
