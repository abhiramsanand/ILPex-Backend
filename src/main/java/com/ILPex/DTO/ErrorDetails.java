package com.ILPex.DTO;


import lombok.*;

import java.util.Date;
@AllArgsConstructor
@Getter
@Setter
@Data
@NoArgsConstructor
public class ErrorDetails {
    private Date timeStamp;
    private String message;
    private String description;
}
