package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentReportDTO {
    private String assessmentName;
    private Boolean assessmentStatus;
    private Long numberOfStudentsAttended; // Changed to Long to match COUNT return type
    private String traineeName;
    private String traineeStatus;
    private Integer score;
}
