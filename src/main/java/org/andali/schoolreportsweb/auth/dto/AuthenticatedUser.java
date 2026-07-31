package org.andali.schoolreportsweb.auth.dto;

import org.andali.schoolreportsweb.enums.UserRole;

public record AuthenticatedUser(
        Long id,
        String name,
        String email,
        UserRole role,
        Long schoolId,
        String schoolName
) {
}
