package org.andali.schoolreportsweb.repository;

import org.andali.schoolreportsweb.model.SchoolClass;
import org.andali.schoolreportsweb.model.SchoolSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolSubjectRepository extends JpaRepository<SchoolSubject, Long> {
    SchoolSubject findAllByName(String name);

    List<SchoolSubject> findAllBySchoolClass(SchoolClass schoolClass);

    boolean existsByNameAndSchoolClass(String name, SchoolClass schoolClass);

    long countBySchoolClass(SchoolClass schoolClass);
}
