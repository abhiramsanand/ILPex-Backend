package com.ILPex.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PendingSubmissionDTO {
    private Long courseId;
    private String courseName;
    private int dayNumber;
}