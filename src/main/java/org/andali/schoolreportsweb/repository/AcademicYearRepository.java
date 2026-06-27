package org.andali.schoolreportsweb.repository;

import org.andali.schoolreportsweb.model.AcademicYear;
import org.andali.schoolreportsweb.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    List<AcademicYear> findAllBySchool(School school);
    Optional<AcademicYear> findBySchoolAndLabel(School school, String label);
    Optional<AcademicYear> findBySchoolAndCurrentTrue(School school);
}
