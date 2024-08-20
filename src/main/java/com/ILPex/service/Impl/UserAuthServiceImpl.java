package com.ILPex.service.Impl;

import com.ILPex.DTO.UserDTO;
import com.ILPex.entity.Users;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public String authenticateAndGetRoleId(UserDTO userDTO) {
        Users user = userRepository.findByUserName(userDTO.getUserName());

        if (user != null && passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return String.valueOf(user.getRoles().getId());
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
