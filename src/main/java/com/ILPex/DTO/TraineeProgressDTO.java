package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeProgressDTO {
    private int dayNumber;
    private String courseName;
    private int duration;
    private int estimatedDuration;
}