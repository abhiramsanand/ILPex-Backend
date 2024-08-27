package com.ILPex.DTO;



import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateDTO {
    private String batchName;
    private Timestamp startDate;
    private Timestamp endDate;
}
