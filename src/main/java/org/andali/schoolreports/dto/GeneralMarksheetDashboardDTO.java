package org.andali.schoolreports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheetDashboardDTO {
    private Long totalMarksheets;
    private Long classesCovered;
    private Long examsCovered;
    private LocalDateTime lastGenerated;
}
