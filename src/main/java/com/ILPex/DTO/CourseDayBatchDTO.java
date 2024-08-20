package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDayBatchDTO {
    private String courseName;
    private int dayNumber;
    private String batchName;
    private String courseDuration;
}
