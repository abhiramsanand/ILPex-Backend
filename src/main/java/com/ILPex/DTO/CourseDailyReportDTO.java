package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDailyReportDTO {
    private Integer dayNumber;
    private String courseName;
    private Integer timeTaken;
    private Long dailyReportId;
}
