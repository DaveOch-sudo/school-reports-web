package org.andali.schoolreports.service;

import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.SchoolSubject;
import org.andali.schoolreports.repository.SchoolSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import java.util.List;

@Service
public class SchoolSubjectService {
    private final SchoolSubjectRepository repository;

    @Autowired
    public SchoolSubjectService(SchoolSubjectRepository repository) {
        this.repository = repository;
    }

    public SchoolSubject findAllBySubjectName(String subjectName) {
        return repository.findAllByName(subjectName);
    }

    public void addSchoolSubject(SchoolSubject schoolSubject) {
        repository.save(schoolSubject);
    }

    public void deleteSchoolSubject(SchoolSubject schoolSubject) {
        repository.delete(schoolSubject);
    }

    public List<SchoolSubject> getAllBySchoolClass(SchoolClass selectedClass) {
        return repository.findAllBySchoolClass(selectedClass);
    }

    public List<SchoolSubject> getAllSubjects() {
        return  repository.findAll();
    }

    public void updateSubject(Long id, SchoolSubject editSubject) {
        SchoolSubject subject = repository.findById(id).get();

        if (editSubject != null) {
            subject = editSubject;
            repository.save(subject);
        }
    }
}
