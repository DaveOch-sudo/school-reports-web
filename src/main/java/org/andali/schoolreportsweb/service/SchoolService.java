package org.andali.schoolreportsweb.service;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.model.School;
import org.andali.schoolreportsweb.repository.SchoolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public School create(School school) {
        return schoolRepository.save(school);
    }

    public School getById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + id));
    }

    public School getByName(String name) {
        return schoolRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("School not found: " + name));
    }

    public List<School> getAll() {
        return schoolRepository.findAll();
    }

    public School update(School school) {
        return schoolRepository.save(school);
    }

    public void delete(Long id) {
        schoolRepository.deleteById(id);
    }
}
