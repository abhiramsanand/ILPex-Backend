package com.ILPex.service.Impl;

import com.ILPex.DTO.UserDTO;
import com.ILPex.DTO.UserPostDTO;
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
        List<Users> userList = userRepository.findByRoles_IdAndIsActiveTrue(1L);
        return userList.stream().map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserPostDTO createUser(UserPostDTO userPostDTO) {
        Users users = modelMapper.map(userPostDTO, Users.class);
        users.setIsActive(true);
        Users newUser = userRepository.save(users);
        return modelMapper.map(newUser, UserPostDTO.class);
    }

    @Override
    public void deleteUser(Long id) {
        Users user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found with id: " + id));
                        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void saveUser(Users user) {
        userRepository.save(user);
    }
}
