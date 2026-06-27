package org.andali.schoolreportsweb.dto;

import lombok.Builder;
import lombok.Data;
import org.andali.schoolreportsweb.model.enums.UserRole;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private Long schoolId;
    private Long assignedClassId;
    private String assignedClassName;
}
