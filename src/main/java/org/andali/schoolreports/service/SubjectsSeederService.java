package org.andali.schoolreports.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.SchoolSubject;
import org.andali.schoolreports.repository.SchoolClassRepository;
import org.andali.schoolreports.repository.SchoolSubjectRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectsSeederService {

    private final SchoolSubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Transactional
    public void seedSubjects() {
        seedMiddleClass();
        seedTopClass();
        seedLowerPrimary();
        seedUpperPrimary();
    }

    /* =========================
       MIDDLE CLASS
       ========================= */
    private void seedMiddleClass() {
        SchoolClass middle = getClassByName("Middle");

        add("Language Activities",
                "Development of speaking, listening and sentence formation skills.",
                middle);

        add("Number Work",
                "Counting, number writing and basic number operations.",
                middle);

        add("Environmental Studies",
                "Learning about home, school, community and surroundings.",
                middle);

        add("Religious Education",
                "Moral values, good behavior and basic religious teachings.",
                middle);

        add("Creative Art",
                "Drawing, coloring and creative hand skills.",
                middle);

        add("Physical Education",
                "Games, body coordination and physical fitness.",
                middle);
    }

    /* =========================
       TOP CLASS
       ========================= */
    private void seedTopClass() {
        SchoolClass top = getClassByName("Top");

        add("English Language",
                "Basic reading, writing and sentence construction.",
                top);

        add("Mathematics",
                "Number operations, shapes and problem solving.",
                top);

        add("Environmental Studies",
                "Health, community and natural environment awareness.",
                top);

        add("Religious Education",
                "Moral instruction and spiritual development.",
                top);

        add("Creative Art",
                "Art, music and creative expression skills.",
                top);

        add("Physical Education",
                "Games, fitness and teamwork activities.",
                top);
    }

    /* =========================
       P1 – P3
       ========================= */
    private void seedLowerPrimary() {
        for (int i = 1; i <= 3; i++) {
            SchoolClass cls = getClassByName("P" + i);

            add("English",
                    "Reading, writing, grammar and communication skills.",
                    cls);

            add("Mathematics",
                    "Number concepts, arithmetic and problem solving.",
                    cls);

            add("Literacy I",
                    "Reading and writing in the local language.",
                    cls);

            add("Literacy II",
                    "Advanced literacy and language comprehension.",
                    cls);

            add("Religious Education",
                    "Moral values and character building.",
                    cls);

            add("Creative Art",
                    "Creative skills including art, music and handwork.",
                    cls);

            add("Physical Education",
                    "Physical fitness, games and coordination.",
                    cls);
        }
    }

    /* =========================
       P4 – P7
       ========================= */
    private void seedUpperPrimary() {
        for (int i = 4; i <= 7; i++) {
            SchoolClass cls = getClassByName("P" + i);

            add("English",
                    "Grammar, comprehension and composition writing.",
                    cls);

            add("Mathematics",
                    "Advanced arithmetic, measurements and problem solving.",
                    cls);

            add("Science",
                    "Living things, health, environment and experiments.",
                    cls);

            add("Social Studies",
                    "History, geography, civics and national studies.",
                    cls);

            add("Religious Education",
                    "Moral responsibility and spiritual growth.",
                    cls);

            if (i < 7) {
                add("Physical Education",
                        "Sports, physical fitness and teamwork.",
                        cls);
            }
        }
    }

    /* =========================
       HELPERS
       ========================= */
    private void add(String name, String description, SchoolClass schoolClass) {
        if (!subjectRepository.existsByNameAndSchoolClass(name, schoolClass)) {
            SchoolSubject subject = new SchoolSubject();
            subject.setName(name);
            subject.setDescription(description);
            subject.setSchoolClass(schoolClass);
            subjectRepository.save(subject);
        }
    }

    private SchoolClass getClassByName(String name) {
        return schoolClassRepository.findByClassName(name)
                .orElseThrow(() -> new IllegalStateException(
                        "SchoolClass not found: " + name));
    }
}
