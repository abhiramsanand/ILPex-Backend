package com.ILPex.controller;
import com.ILPex.DTO.TrainingDaysCompletedForTraineeDTO;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.service.TraineeProgressService;
import com.ILPex.response.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class TraineeProgressControllerTest {

    @Mock
    private TraineeProgressService traineeProgressService;

    @InjectMocks
    private TraineeProgressController traineeProgressController;

    @Mock
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(traineeProgressController)
                .setControllerAdvice(new ResponseHandler())
                .build();
    }

    @Test
    @DisplayName("Test getProgress - Success")
    void givenValidRequest_whenGetProgress_thenReturnsProgressList() throws Exception {
        // Given
        TrainingDaysCompletedForTraineeDTO dto = new TrainingDaysCompletedForTraineeDTO();
        dto.setTraineeProgressId(1L);
        dto.setTraineeId(1L);
        dto.setDayNumber(7);

        when(traineeProgressService.getNumberOfDays()).thenReturn(Collections.singletonList(dto));

        // When & Then
        mockMvc.perform(get("/api/v1/progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].traineeProgressId").value(1L))
                .andExpect(jsonPath("$[0].traineeId").value(1L))
                .andExpect(jsonPath("$[0].dayNumber").value(7));
    }
    @Test
    @DisplayName("Test getDayNumberById - Success")
    void givenValidId_whenGetDayNumberById_thenReturnsDTO() throws Exception {
        // Given
        Long traineeId = 1L;
        TrainingDaysCompletedForTraineeDTO dto = new TrainingDaysCompletedForTraineeDTO();
        dto.setTraineeProgressId(1L);
        dto.setTraineeId(traineeId);
        dto.setDayNumber(7);

        when(traineeProgressService.getDayNumberById(traineeId)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/progress/{id}", traineeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.traineeProgressId").value(1L))
                .andExpect(jsonPath("$.data.traineeId").value(traineeId))
                .andExpect(jsonPath("$.data.dayNumber").value(7))
                .andExpect(jsonPath("$.message").value(" Requested Trainee Details are given here"));
    }
    @Test
    @DisplayName("Test getDayNumberById - Resource Not Found")
    void givenInvalidId_whenGetDayNumberById_thenReturnsNotFound() throws Exception {
        // Given
        long invalidTraineeId = 999L;  // An ID that doesn't exist
        when(traineeProgressService.getDayNumberById(invalidTraineeId))
                .thenThrow(new ResourceNotFoundException("TraineeProgress", "id", invalidTraineeId));

        // When & Then
        mockMvc.perform(get("/api/v1/progress/{id}", invalidTraineeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())  // Print the response for debugging
                .andExpect(status().isNotFound());
    }



}

