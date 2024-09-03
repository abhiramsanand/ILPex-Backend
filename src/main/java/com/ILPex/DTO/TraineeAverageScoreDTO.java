package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeAverageScoreDTO {
    private Long traineeId;
    private Double averageScore;
}