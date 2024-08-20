package com.ILPex.service.Impl;

import com.ILPex.DTO.UserDTO;
import com.ILPex.entity.Users;
import com.ILPex.repository.RoleRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<UserDTO> getUsers() {
        List<Users> userList = userRepository.findByRoles_Id(1L);
        return userList.stream().map(user -> {
            return modelMapper.map(user, UserDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        Users users = modelMapper.map(userDTO,Users.class);
        Users newUser = userRepository.save(users);
        UserDTO newUserDto =modelMapper.map(newUser,UserDTO.class);
        return newUserDto;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

}
