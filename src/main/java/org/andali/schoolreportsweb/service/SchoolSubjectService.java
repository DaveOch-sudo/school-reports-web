package org.andali.schoolreportsweb.service;

import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.model.SchoolSubject;
import org.andali.schoolreportsweb.repository.SchoolSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
