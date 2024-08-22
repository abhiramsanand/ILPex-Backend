package com.ILPex.DTO;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeAssessmentDisplayDTO {
    private String assessmentName;
    private List<QuestionsDTO> questions;
}
