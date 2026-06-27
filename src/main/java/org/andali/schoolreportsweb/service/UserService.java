package org.andali.schoolreportsweb.service;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.model.School;
import org.andali.schoolreportsweb.model.User;
import org.andali.schoolreportsweb.model.enums.UserRole;
import org.andali.schoolreportsweb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    public List<User> getAllBySchool(School school) {
        return userRepository.findAllBySchool(school);
    }

    public List<User> getTeachers(School school) {
        return userRepository.findAllBySchoolAndRole(school, UserRole.TEACHER);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
