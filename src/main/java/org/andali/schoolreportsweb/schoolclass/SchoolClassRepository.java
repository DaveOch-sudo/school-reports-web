package org.andali.schoolreportsweb.schoolclass;

import org.andali.schoolreportsweb.school.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllBySchool(School school);
    Optional<SchoolClass> findByName(String name);

    @Query("SELECT c FROM SchoolClass c WHERE c.id = :id")
    SchoolClass findByIdWithDetails(@Param("id") Long classId);
}
