package com.ILPex.service;

import com.ILPex.DTO.UserDTO;
import com.ILPex.DTO.UserPostDTO;
import com.ILPex.entity.Users;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsers();
    UserPostDTO createUser(UserPostDTO userPostDTO);
    void deleteUser(Long id);
     void saveUser(Users user);
}
