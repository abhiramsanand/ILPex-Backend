package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDailyReportDTO {
    private Long traineeId;
    private String traineeName;
    private Long totalCourses;
    private Long totalDailyReports;
}
