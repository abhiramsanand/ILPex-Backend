package com.ILPex.DTO;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DailyReportCreationDTO {
    private Long id;
    private String courseName;
    private LocalDateTime timeTaken;
    private String keyLearnings;
    private String planForTomorrow;
    private Long courseId;

}
