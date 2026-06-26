package org.andali.schoolreportsweb.service;

import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
