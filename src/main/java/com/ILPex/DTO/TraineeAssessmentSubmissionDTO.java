package com.ILPex.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TraineeAssessmentSubmissionDTO {
    private Long assessmentId;
    private int assessmentBatchAllocationId;
    private int traineeId;
    private List<String> responses;
}
