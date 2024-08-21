package com.ILPex.DTO;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDetailsDTO {
    private String assessmentName;
    private Long batchId;
    private String assessmentStatus;
    private Long traineeCount;
}