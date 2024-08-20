package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeReportStatusDTO {
    private Long traineeId;
    private String traineeName;
    private String courseName;
    private int lastSubmittedDayNumber;
    private int expectedDayNumber;
    private boolean isLagging;
    private Long batchId;
}
