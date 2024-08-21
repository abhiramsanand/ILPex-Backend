package com.ILPex.DTO;


import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineePendingAssessmentDTO {
    private String assessmentName;
    private Timestamp dueDate;
    private Long traineeId;


}
