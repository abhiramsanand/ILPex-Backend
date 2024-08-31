package com.ILPex.DTO;

import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDTO {
    private Long assessmentId;
    private Long traineeId;
    private Map<Long, String> questionResponses;
}