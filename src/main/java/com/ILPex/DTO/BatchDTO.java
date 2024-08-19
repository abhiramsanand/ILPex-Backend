package com.ILPex.DTO;

import jakarta.persistence.Column;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BatchDTO {
    private Long id;
    private String batchName;
    private Timestamp startDate;
    private Timestamp endDate;
    private Boolean isActive;
}
