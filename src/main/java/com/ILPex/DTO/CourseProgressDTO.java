package com.ILPex.DTO;

import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressDTO {
    private String courseName;
    private int dayNumber;
    private String courseDuration;
    private int traineeDuration;
    private double completionPercentage;
}
