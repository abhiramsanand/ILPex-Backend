package com.ILPex.service;

import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.repository.TraineeAssessmentRepository;
import com.ILPex.service.Impl.TraineeAssessmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.repository.TraineeAssessmentRepository;
import com.ILPex.service.Impl.TraineeAssessmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

class TraineeAssessmentServiceImplTest {

    @Mock
    private TraineeAssessmentRepository traineeAssessmentRepository;

    @InjectMocks
    private TraineeAssessmentServiceImpl traineeAssessmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenTraineeAssessments_whenFetchingDetails_thenReturnListOfAssessments(){
        // Given
        TraineeAssessmentDTO assessment1 = new TraineeAssessmentDTO("Trainee1", 85, "Passed", "Assessment1");
        TraineeAssessmentDTO assessment2 = new TraineeAssessmentDTO("Trainee2", 90, "Passed", "Assessment2");

        List<TraineeAssessmentDTO> expectedAssessments = Arrays.asList(assessment1, assessment2);

        // When
        when(traineeAssessmentRepository.fetchTraineeAssessmentDetails()).thenReturn(expectedAssessments);

        // Then
        List<TraineeAssessmentDTO> actualAssessments = traineeAssessmentService.getTraineeAssessmentDetails();
        assertEquals(expectedAssessments, actualAssessments, "The fetched trainee assessments should match the expected list.");
    }


    @Test
    void givenTraineeAssessments_whenFetchingDetails_thenReturnIncorrectListOfAssessments() {
        // Given
        TraineeAssessmentDTO assessment1 = new TraineeAssessmentDTO("Trainee1", 85, "Passed", "Assessment1");
        TraineeAssessmentDTO assessment2 = new TraineeAssessmentDTO("Trainee2", 90, "Passed", "Assessment2");

        List<TraineeAssessmentDTO> expectedAssessments = Arrays.asList(assessment1, assessment2);

        // When
        // Returning a different list to simulate failure
        TraineeAssessmentDTO incorrectAssessment = new TraineeAssessmentDTO("Trainee3", 75, "Failed", "Assessment3");
        when(traineeAssessmentRepository.fetchTraineeAssessmentDetails()).thenReturn(Arrays.asList(incorrectAssessment));

        // Then
        List<TraineeAssessmentDTO> actualAssessments = traineeAssessmentService.getTraineeAssessmentDetails();

        // Assert that the actual result is NOT equal to the expected result
        assertNotEquals(expectedAssessments, actualAssessments, "Expected and actual trainee assessments should not match.");
    }



}
