package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeLaggingPercentageDTO {
    private Long batchId;
    private double laggingPercentage;
}
