package org.andali.schoolreportsweb.repository;

import org.andali.schoolreportsweb.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    SchoolClass findAllByClassName(String schoolClassName);

    @Query("""
        SELECT c FROM SchoolClass c
        LEFT JOIN FETCH c.students
        WHERE c.id = :id
    """)
    SchoolClass findByIdWithDetails(@Param("id") Long classId);


    Optional<SchoolClass> findByClassName(String className);
}
