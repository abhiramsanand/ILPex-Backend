package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeCompletedAssessmentDTO {
    private String assessmentName;
    private int score;
    private int traineeId;
}
