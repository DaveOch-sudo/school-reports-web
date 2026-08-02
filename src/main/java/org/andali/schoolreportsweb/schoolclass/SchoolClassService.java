package org.andali.schoolreportsweb.schoolclass;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.exception.ResourceNotFoundException;
import org.andali.schoolreportsweb.school.School;
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
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
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
