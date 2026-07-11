package org.andali.schoolreportsweb.user;

import org.andali.schoolreportsweb.enums.UserRole;
import org.andali.schoolreportsweb.school.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllBySchool(School school);
    List<User> findAllBySchoolAndRole(School school, UserRole role);
    boolean existsByEmail(String email);
}
