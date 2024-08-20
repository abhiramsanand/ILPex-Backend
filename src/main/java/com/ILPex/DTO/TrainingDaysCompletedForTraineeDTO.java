package com.ILPex.DTO;

import lombok.Data;

@Data
public class TrainingDaysCompletedForTraineeDTO {
    private Long traineeProgressId;
    private Long traineeId;
    private int dayNumber;

}
