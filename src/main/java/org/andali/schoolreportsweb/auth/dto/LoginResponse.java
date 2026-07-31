package org.andali.schoolreportsweb.auth.dto;

public record LoginResponse(
        String token,
        AuthenticatedUser user
) {
}
