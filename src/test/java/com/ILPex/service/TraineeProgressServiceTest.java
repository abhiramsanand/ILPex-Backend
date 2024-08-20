package com.ILPex.service;

import com.ILPex.DTO.TrainingDaysCompletedForTraineeDTO;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.service.Impl.TraineeProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TraineeProgressServiceTest {
    @Mock
    private TraineeProgressRepository traineeProgressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TraineeProgressServiceImpl traineeProgressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void givenTraineeProgress_whenGetNumberOfDays_thenReturnTrainingDaysCompletedForTraineeDTO() {
        // Given
        TraineeProgress progress1 = new TraineeProgress();
        progress1.setId(1L);
        progress1.setDayNumber(5);

        TraineeProgress progress2 = new TraineeProgress();
        progress2.setId(2L);
        progress2.setDayNumber(10);

        List<TraineeProgress> progressList = Arrays.asList(progress1, progress2);

        TrainingDaysCompletedForTraineeDTO dto1 = new TrainingDaysCompletedForTraineeDTO();
        dto1.setTraineeId(1L);
        dto1.setDayNumber(5);

        TrainingDaysCompletedForTraineeDTO dto2 = new TrainingDaysCompletedForTraineeDTO();
        dto2.setTraineeId(2L);
        dto2.setDayNumber(10);

        List<TrainingDaysCompletedForTraineeDTO> dtoList = Arrays.asList(dto1, dto2);

        when(traineeProgressRepository.findAll()).thenReturn(progressList);
        when(modelMapper.map(progress1, TrainingDaysCompletedForTraineeDTO.class)).thenReturn(dto1);
        when(modelMapper.map(progress2, TrainingDaysCompletedForTraineeDTO.class)).thenReturn(dto2);

        // When
        List<TrainingDaysCompletedForTraineeDTO> result = traineeProgressService.getNumberOfDays();

        // Then
        assertEquals(dtoList, result);
        verify(traineeProgressRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(progress1, TrainingDaysCompletedForTraineeDTO.class);
        verify(modelMapper, times(1)).map(progress2, TrainingDaysCompletedForTraineeDTO.class);
    }
    @Test
    @DisplayName("Test getDayNumberById - Success")
    void givenValidId_whenGetDayNumberById_thenReturnsDTO() {
        // Given
        Long traineeId = 1L;

        // Create a TraineeProgress instance
        TraineeProgress traineeProgress = new TraineeProgress();

        traineeProgress.setDayNumber(7);

        // Create a DTO instance
        TrainingDaysCompletedForTraineeDTO dto = new TrainingDaysCompletedForTraineeDTO();
        dto.setTraineeProgressId(1L);
        dto.setTraineeId(traineeId);
        dto.setDayNumber(7);

        // Set up the mock
        when(traineeProgressRepository.findById(traineeId)).thenReturn(Optional.of(traineeProgress));
        when(modelMapper.map(traineeProgress, TrainingDaysCompletedForTraineeDTO.class)).thenReturn(dto);

        // When
        TrainingDaysCompletedForTraineeDTO result = traineeProgressService.getDayNumberById(traineeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTraineeProgressId()).isEqualTo(1L);
        assertThat(result.getTraineeId()).isEqualTo(traineeId);
        assertThat(result.getDayNumber()).isEqualTo(7);
    }

    @Test
    @DisplayName("Test getDayNumberById - resource not found")
    void givenInvalidId_whenGetDayNumberById_thenThrowsException() {
        // Given
        Long traineeId = 1L;

        when(traineeProgressRepository.findById(traineeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            traineeProgressService.getDayNumberById(traineeId);
        });
    }
}
