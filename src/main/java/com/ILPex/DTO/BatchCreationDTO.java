package com.ILPex.DTO;

import com.ILPex.entity.Trainees;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchCreationDTO {
    private String batchName;
    private Timestamp startDate;
    private Timestamp endDate;
    private Boolean isActive;
    private Set<Trainees> trainees;
}
