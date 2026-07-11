package org.andali.schoolreportsweb.generalmarksheet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMarksheetDashboardDTO {
    private Long totalMarksheets;
    private Long classesCovered;
    private Long examsCovered;
    private LocalDateTime lastGenerated;
}
