package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultDTO {
    private int score;
    private int correctAnswers;
    private int incorrectAnswers;
}