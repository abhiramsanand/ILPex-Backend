package com.ILPex.DTO;

import lombok.*;

import java.time.LocalDate;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayNumberWithDateDTO {
    private LocalDate date;
    private int dayNumber;
}
