package com.ILPex.DTO;

import lombok.*;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DailyReportEditDTO {
    private String courseName;
    private Integer timeTaken;
    private String keylearnings;
    private String planfortomorrow;
}
