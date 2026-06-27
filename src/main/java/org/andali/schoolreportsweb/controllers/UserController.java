package org.andali.schoolreportsweb.controllers;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.dto.UserDto;
import org.andali.schoolreportsweb.mapper.UserMapper;
import org.andali.schoolreportsweb.model.User;
import org.andali.schoolreportsweb.service.SchoolClassService;
import org.andali.schoolreportsweb.service.SchoolService;
import org.andali.schoolreportsweb.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SchoolService schoolService;
    private final SchoolClassService schoolClassService;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<UserDto>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(userService.getAllBySchool(schoolService.getById(schoolId))
                .stream().map(userMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toDto(userService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setPasswordHash(""); // placeholder — will be set during auth registration
        if (dto.getSchoolId() != null) user.setSchool(schoolService.getById(dto.getSchoolId()));
        if (dto.getAssignedClassId() != null)
            user.setAssignedClass(schoolClassService.getSchoolClassById(dto.getAssignedClassId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDto(userService.create(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        User user = userService.getById(id);
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        if (dto.getAssignedClassId() != null)
            user.setAssignedClass(schoolClassService.getSchoolClassById(dto.getAssignedClassId()));
        else user.setAssignedClass(null);
        return ResponseEntity.ok(userMapper.toDto(userService.update(user)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
