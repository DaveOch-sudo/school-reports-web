package org.andali.schoolreportsweb.year;

import org.andali.schoolreportsweb.school.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    List<AcademicYear> findAllBySchool(School school);
    Optional<AcademicYear> findBySchoolAndLabel(School school, String label);
    Optional<AcademicYear> findBySchoolAndIsCurrentTrue(School school);
}
