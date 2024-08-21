package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDetailsDTO {
    private String assessmentName;
    private String assessmentStatus;
    private Long traineeCount;
}
