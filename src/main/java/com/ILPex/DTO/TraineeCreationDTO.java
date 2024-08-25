package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeCreationDTO {
    private String userName;
    private String role;
    private String email;
    private String percipioEmail;
    private String password;
}
