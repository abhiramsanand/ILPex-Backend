package com.ILPex.service;
import com.ILPex.DTO.UserDTO;
import com.ILPex.DTO.UserPostDTO;
import com.ILPex.entity.Users;
import com.ILPex.repository.RoleRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest{

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenValidRoleId_whenGetUsers_thenReturnUserDtoList() {
        // Given
        Users user1 = new Users();
        user1.setId(1L);
        user1.setUserName("John Doe");

        Users user2 = new Users();
        user2.setId(2L);
        user2.setUserName("Jane Smith");

        List<Users> userList = Arrays.asList(user1, user2);
        UserDTO userDTO1 = new UserDTO(1L, "John Doe", "john.doe@example.com", "password", null, null, null);
        UserDTO userDTO2 = new UserDTO(2L, "Jane Smith", "jane.smith@example.com", "password", null, null, null);

        when(userRepository.findByRoles_Id(1L)).thenReturn(userList);
        when(modelMapper.map(user1, UserDTO.class)).thenReturn(userDTO1);
        when(modelMapper.map(user2, UserDTO.class)).thenReturn(userDTO2);

        // When
        List<UserDTO> result = userService.getUsers();

        // Debugging
        System.out.println(result);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getUserName());
        assertEquals("Jane Smith", result.get(1).getUserName());
    }


    @Test
    public void givenValidUserPostDto_whenCreateUser_thenReturnUserPostDto() {
        // Given
        UserPostDTO userPostDTO = new UserPostDTO(1L, "John Doe", "john.doe@example.com","password","1");
        Users users = new Users();
        users.setId(1L);
        users.setUserName("John Doe");

        when(modelMapper.map(userPostDTO, Users.class)).thenReturn(users);
        when(userRepository.save(any(Users.class))).thenReturn(users);
        when(modelMapper.map(users, UserPostDTO.class)).thenReturn(userPostDTO);

        // When
        UserPostDTO result = userService.createUser(userPostDTO);

        // Then
        assertNotNull(result);
        assertEquals(userPostDTO.getUserName(), result.getUserName());
        assertEquals(userPostDTO.getEmail(), result.getEmail());
    }

    @Test
    public void givenValidUserId_whenDeleteUser_thenSuccess() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When & Then
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void givenInvalidUserId_whenDeleteUser_thenThrowException() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User not found with id: " + userId, thrown.getMessage());
    }

}
