package com.ILPex.DTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DailyReportDTO {
    private Long id;
    private Long courseId;
    private Date date;
    private String courseName;
    private Integer timeTaken;
}
