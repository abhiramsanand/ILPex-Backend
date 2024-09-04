package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseTraineeProgressDTO {
    private int dayNumber;
    private String courseName;
    private String courseDuration;
    private int duration;

}