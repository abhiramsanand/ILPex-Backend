package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeActualVsEstimatedDurationDTO {
    private String username;
    private Long totalDuration;
    private Long totalEstimatedDuration;

}
