package com.ILPex.DTO;

import lombok.*;

import java.sql.Timestamp;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String email;
    private String password;
}

