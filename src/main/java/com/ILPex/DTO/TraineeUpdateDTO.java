package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeUpdateDTO {
    private int traineeId;
    private String userName;
    private String email;
    private String percipioEmail;
    private String password;
}
