package com.ILPex.service;

import com.ILPex.DTO.AssessmentNotificationDTO;

import java.util.List;

public interface AssessmentNotificationService {
    void notifyAllTraineesOfBatch(Long batchId, Long assessmentBatchAllocationId);
    List<AssessmentNotificationDTO> getNotificationsForTrainee(Long traineeId);
    void markNotificationAsRead(Long id);
}
