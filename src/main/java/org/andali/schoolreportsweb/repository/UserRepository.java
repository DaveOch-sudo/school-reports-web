package org.andali.schoolreportsweb.repository;

import org.andali.schoolreportsweb.model.School;
import org.andali.schoolreportsweb.model.User;
import org.andali.schoolreportsweb.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllBySchool(School school);
    List<User> findAllBySchoolAndRole(School school, UserRole role);
    boolean existsByEmail(String email);
}
