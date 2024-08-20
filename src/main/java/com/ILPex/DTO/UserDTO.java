package com.ILPex.DTO;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private Timestamp lastAccess;
    private String rolesName;
    private String rolesId;
}

