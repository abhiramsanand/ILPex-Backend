package com.ILPex.DTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DailyReportAddDTO {
    private LocalDate date;
    private String keyLearnings;
    private String planForTomorrow;
    private ReportStatus status;  // Enum for status ('SUBMITTED', 'PENDING')

    // Enum for ReportStatus
    public enum ReportStatus {
        SUBMITTED,
        PENDING
    }
    private LocalDateTime timeTaken;
    private Long courseId;
    private Long traineeId;

}

