package com.ILPex.service.Impl;

import com.ILPex.DTO.UserDTO;
import com.ILPex.entity.Users;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String authenticateAndGetRoleId(UserDTO userDTO) {
        Users user = userRepository.findByUserName(userDTO.getUserName());

        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!user.getPassword().equals(userDTO.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        Long roleId = user.getRoles().getId();
        if (roleId == 3) { // Trainee role
            Long traineeId = user.getTrainees().stream().findFirst().orElseThrow(
                    () -> new RuntimeException("No trainee record found for the user")
            ).getId();
            return String.format("RoleID: %d, TraineeID: %d", roleId, traineeId);
        } else {
            return String.format("RoleID: %d", roleId);
        }
    }
}


