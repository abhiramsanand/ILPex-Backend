package com.ILPex.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDTO {
    private String name;
    private String role;
    private String email;
    private String percipioEmail;
    private String password;
}
