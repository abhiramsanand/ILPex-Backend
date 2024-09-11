package com.ILPex.service;
import com.ILPex.entity.Batches;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeDurationService;
import com.ILPex.service.Impl.TraineeDurationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TraineeDurationServiceTest {

    @Mock
    private TraineesRepository traineesRepository;

    @InjectMocks
    private TraineeDurationServiceImpl traineeDurationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenBatchWithNoAcceleratedTrainees_whenCalculateAcceleratedPercentage_thenReturnZero() {
        // Given
        Long batchId = 1L;

        TraineeProgress progress = new TraineeProgress();
        progress.setDuration(12);
        progress.setEstimatedDuration(10);

        Trainees trainee = new Trainees();
        trainee.setTraineeProgresses(Set.of(progress));

        List<Trainees> traineesList = List.of(trainee);

        when(traineesRepository.findByBatchesId(batchId)).thenReturn(traineesList);

        // When
        double percentage = traineeDurationService.calculateAcceleratedPercentage(batchId);

        // Then
        assertEquals(0.0, percentage, 0.01); // No trainees are accelerated
    }

    @Test
    void givenEmptyBatch_whenCalculateAcceleratedPercentage_thenReturnZero() {
        // Given
        Long batchId = 1L;

        when(traineesRepository.findByBatchesId(batchId)).thenReturn(List.of());

        // When
        double percentage = traineeDurationService.calculateAcceleratedPercentage(batchId);

        // Then
        assertEquals(0.0, percentage, 0.01); // No trainees in the batch
    }


}

