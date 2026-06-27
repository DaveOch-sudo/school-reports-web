package org.andali.schoolreportsweb.service;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.model.SchoolSubject;
import org.andali.schoolreportsweb.repository.SchoolSubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolSubjectService {

    private final SchoolSubjectRepository repository;

    public List<SchoolSubject> getAllSubjects() {
        return repository.findAll();
    }

    public List<SchoolSubject> getAllBySchoolClass(SchoolClass schoolClass) {
        return repository.findAllBySchoolClass(schoolClass);
    }

    public SchoolSubject getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));
    }

    public SchoolSubject save(SchoolSubject subject) {
        return repository.save(subject);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
