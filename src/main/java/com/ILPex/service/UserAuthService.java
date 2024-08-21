package com.ILPex.service;

import com.ILPex.DTO.UserDTO;

public interface UserAuthService {
    String authenticateAndGetRoleId(UserDTO userDTO);
}
