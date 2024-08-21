package com.ILPex.service;

import com.ILPex.DTO.UserDTO;
import com.ILPex.DTO.UserPostDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsers();
    UserPostDTO createUser(UserPostDTO userPostDTO);
    void deleteUser(Long id);
}
