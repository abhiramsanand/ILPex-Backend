package com.ILPex.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TraineeAssessmentDTO {
    private String traineeName;
    private Integer traineeScore;
    private String traineeStatus;
    private String assessmentName;
}