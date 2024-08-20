package com.ILPex.service;

import com.ILPex.DTO.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsers();
    UserDTO createUser(UserDTO userDTO);
    void deleteUser(Long id);
}
