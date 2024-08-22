package com.ILPex.DTO;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DailyReportDTO {
    private Long id;
    private String courseName;
    private LocalDateTime timeTaken;
}
