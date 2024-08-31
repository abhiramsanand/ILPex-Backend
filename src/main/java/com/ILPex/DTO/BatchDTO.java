package com.ILPex.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
public class BatchDTO {
    private Long id;
    private String batchName;
    private Timestamp startDate;
    private Timestamp endDate;
    private Boolean isActive;
    private Long dayNumber;
    private Integer totalTrainees;
}
