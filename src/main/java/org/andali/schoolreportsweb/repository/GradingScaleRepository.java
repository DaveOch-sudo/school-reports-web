package org.andali.schoolreportsweb.repository;

import org.andali.schoolreportsweb.model.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    @Query("""
        SELECT gs FROM GradingScale gs
        LEFT JOIN FETCH gs.steps
        WHERE gs.id = :id
    """)
    GradingScale findGradingScaleById(@Param("id") Long id);

    @Query("""
    SELECT gs FROM GradingScale gs
    LEFT JOIN FETCH gs.steps
""")
    List<GradingScale> findAllWithSteps();

}
