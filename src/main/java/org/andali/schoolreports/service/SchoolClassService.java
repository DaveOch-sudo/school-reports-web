package org.andali.schoolreports.service;

import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchoolClassService {
    private final SchoolClassRepository schoolClassRepository;


    public SchoolClassService(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<SchoolClass> getAllSchoolClasses() {
        return schoolClassRepository.findAll();
    }

    public void deleteSchoolClass(SchoolClass schoolClass) {
        schoolClassRepository.delete(schoolClass);
    }

    public SchoolClass getSchoolClassByName(String schoolClassName) {
        return schoolClassRepository.findAllByClassName(schoolClassName);
    }
    public void AddSchoolClass(SchoolClass schoolClass) {
        schoolClassRepository.save(schoolClass);
    }

    public SchoolClass getSchoolClassById(Long classId) {
        return schoolClassRepository.findByIdWithDetails(classId);
    }
}
