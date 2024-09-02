package com.ILPex.DTO;

import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDailyReportDTO {
    private Integer dayNumber;
    private Timestamp courseDate;
    private String courseName;
    private Integer timeTaken;
    private Long dailyReportId;
}
