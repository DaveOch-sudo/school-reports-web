package org.andali.schoolreportsweb.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationError(
        LocalDateTime timestamp,
        int status,
        Map<String, String> errors
) {
}
