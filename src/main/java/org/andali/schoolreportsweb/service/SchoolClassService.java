package org.andali.schoolreportsweb.service;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.model.School;
import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public List<SchoolClass> getAllSchoolClasses() {
        return schoolClassRepository.findAll();
    }

    public List<SchoolClass> getAllBySchool(School school) {
        return schoolClassRepository.findAllBySchool(school);
    }

    public SchoolClass getSchoolClassById(Long id) {
        return schoolClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + id));
    }

    public SchoolClass getSchoolClassByName(String name) {
        return schoolClassRepository.findByName(name).orElse(null);
    }

    public SchoolClass save(SchoolClass schoolClass) {
        return schoolClassRepository.save(schoolClass);
    }

    public void deleteById(Long id) {
        schoolClassRepository.deleteById(id);
    }
}
