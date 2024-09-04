package com.ILPex.service;

import com.ILPex.entity.AssessmentBatchAllocation;
import com.ILPex.entity.AssessmentNotification;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.AssessmentBatchAllocationRepository;
import com.ILPex.repository.AssessmentNotificationRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.Impl.AssessmentNotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssessmentNotificationServiceImplTest {

    @Mock
    private AssessmentNotificationRepository assessmentNotificationRepository;

    @Mock
    private TraineesRepository traineesRepository;

    @Mock
    private AssessmentBatchAllocationRepository assessmentBatchAllocationRepository;

    @InjectMocks
    private AssessmentNotificationServiceImpl assessmentNotificationService;

    public AssessmentNotificationServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenBatchIdAndAllocationId_whenNotifyAllTrainees_thenCreateNotifications() {
        // Given
        Long batchId = 1L;
        Long assessmentBatchAllocationId = 1L;

        Trainees trainee1 = new Trainees();
        Trainees trainee2 = new Trainees();
        List<Trainees> trainees = Arrays.asList(trainee1, trainee2);

        AssessmentBatchAllocation assessmentBatchAllocation = new AssessmentBatchAllocation();
        when(assessmentBatchAllocationRepository.findById(assessmentBatchAllocationId))
                .thenReturn(Optional.of(assessmentBatchAllocation));

        when(traineesRepository.findByBatchesId(batchId)).thenReturn(trainees);

        // When
        assessmentNotificationService.notifyAllTraineesOfBatch(batchId, assessmentBatchAllocationId);

        // Then
        verify(assessmentNotificationRepository, times(trainees.size())).save(any(AssessmentNotification.class));
    }


}
