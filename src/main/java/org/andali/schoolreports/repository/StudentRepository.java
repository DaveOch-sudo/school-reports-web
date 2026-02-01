package org.andali.schoolreports.repository;

import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findAllByName(String name);

    Student findAllByNameAndSchoolClass(String name, SchoolClass schoolClass);

    List<Student> findByName(String name);

    List<Student> findBySchoolClass(SchoolClass schoolClass);

    Optional<Student> findById(Long id);

    List<Student> findBySchoolClass_Id(Long schoolClassId);
}
