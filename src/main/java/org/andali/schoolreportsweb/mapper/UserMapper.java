package org.andali.schoolreportsweb.mapper;

import org.andali.schoolreportsweb.dto.UserDto;
import org.andali.schoolreportsweb.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .schoolId(u.getSchool() != null ? u.getSchool().getId() : null)
                .assignedClassId(u.getAssignedClass() != null ? u.getAssignedClass().getId() : null)
                .assignedClassName(u.getAssignedClass() != null ? u.getAssignedClass().getName() : null)
                .build();
    }
}
