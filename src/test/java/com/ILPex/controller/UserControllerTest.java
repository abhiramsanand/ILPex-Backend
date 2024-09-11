package com.ILPex.controller;

import com.ILPex.DTO.UserDTO;
import com.ILPex.DTO.UserPostDTO;
import com.ILPex.service.UserService;
import com.ILPex.service.UserAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserAuthService userAuthService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // Not needed with @ExtendWith(MockitoExtension.class)
    }

    @Test
    public void givenValidRequest_whenGetUsers_thenReturnUserDtoList() throws Exception {
        // Given
        UserDTO userDTO1 = new UserDTO(1L, "John Doe", "john.doe@example.com", "password",null, null, null, null);
        UserDTO userDTO2 = new UserDTO(2L, "Jane Smith", "jane.smith@example.com", "password",null, null, null,null);

        when(userService.getUsers()).thenReturn(List.of(userDTO1, userDTO2));

        // When & Then
        mockMvc.perform(get("/api/v1/users/view")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName").value("John Doe"))
                .andExpect(jsonPath("$[1].userName").value("Jane Smith"));
    }

    @Test
    public void givenValidUserPostDto_whenCreateUser_thenReturnUserPostDto() throws Exception {
        // Given
        UserPostDTO userPostDTO = new UserPostDTO(1L, "John Doe", "john.doe@example.com", "password", "1");
        UserPostDTO createdUserPostDTO = new UserPostDTO(1L, "John Doe", "john.doe@example.com", "password", "1");

        when(userService.createUser(any(UserPostDTO.class))).thenReturn(createdUserPostDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void givenValidUserId_whenDeleteUser_thenReturnNoContentStatus() throws Exception {
        // Given
        Long userId = 1L;

        // Mock the delete behavior if necessary
        // when(userService.deleteUser(userId)).thenReturn(void);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/delete/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
