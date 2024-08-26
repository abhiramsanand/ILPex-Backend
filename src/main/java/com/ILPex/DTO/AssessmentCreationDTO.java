package com.ILPex.DTO;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssessmentCreationDTO {

    private String title;
    private Long batchId;
    private String startDate;
    private String endDate;
    private List<QuestionCreationDTO> questions;
}
