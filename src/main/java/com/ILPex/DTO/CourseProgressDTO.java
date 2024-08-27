package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressDTO {
    private String courseName;
    private Integer dayNumber;
    private Integer estimatedDuration;
    private Integer duration;
    private Integer percentageCompleted;
}

