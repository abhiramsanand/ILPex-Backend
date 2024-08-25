package com.ILPex.DTO;

import com.ILPex.entity.DailyReports;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class DailyReportRequestDTO {

  private Integer timeTaken;
    private String keyLearnings;
    private String planForTomorrow;

}
