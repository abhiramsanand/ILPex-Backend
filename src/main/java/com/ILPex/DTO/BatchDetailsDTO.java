package com.ILPex.DTO;



import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchDetailsDTO {
    private Long batchId;
    private String batchName;
    private String programName;
    private boolean isActive;
    private String startDate;
    private String endDate;
    private int numberOfTrainees;
    private List<TraineeDisplayByBatchDTO> trainees;

}
