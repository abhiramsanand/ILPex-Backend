package com.ILPex.DTO;

import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchAndTraineeUpdateDTO {
    private BatchDetailsDTO batchDetails;
    private List<TraineeUpdateDTO> trainees;



}
