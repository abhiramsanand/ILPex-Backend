package com.ILPex.service.Impl;

import com.ILPex.DTO.AssessmentNotificationDTO;
import com.ILPex.entity.AssessmentBatchAllocation;
import com.ILPex.entity.AssessmentNotification;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.AssessmentBatchAllocationRepository;
import com.ILPex.repository.AssessmentNotificationRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.AssessmentNotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentNotificationServiceImpl implements AssessmentNotificationService {

    @Autowired
    private AssessmentNotificationRepository assessmentNotificationRepository;

    @Autowired
    private TraineesRepository traineesRepository;

    @Autowired
    private AssessmentBatchAllocationRepository assessmentBatchAllocationRepository;

    @Override
    public void notifyAllTraineesOfBatch(Long batchId, Long assessmentBatchAllocationId) {
        // Fetch the AssessmentBatchAllocation by ID
        AssessmentBatchAllocation assessmentBatchAllocation = assessmentBatchAllocationRepository
                .findById(assessmentBatchAllocationId)
                .orElseThrow(() -> new RuntimeException("AssessmentBatchAllocation not found"));

        // Fetch all trainees belonging to the batch
        List<Trainees> trainees = traineesRepository.findByBatchesId(batchId);

        // Create and save a notification for each trainee
        for (Trainees trainee : trainees) {
            AssessmentNotification notification = new AssessmentNotification();
            notification.setAssessmentBatchAllocation(assessmentBatchAllocation);
            notification.setTrainee(trainee);
            notification.setNotificationTime(LocalDateTime.now());
            notification.setIsRead(false);

            assessmentNotificationRepository.save(notification);
        }
    }

    @Override
    public List<AssessmentNotificationDTO> getNotificationsForTrainee(Long traineeId) {
        List<AssessmentNotification> notifications = assessmentNotificationRepository.findByTraineeId(traineeId);
        return notifications.stream()
                .map(notification -> new AssessmentNotificationDTO(notification.getId(),
                        notification.getAssessmentBatchAllocation().getAssessments().getAssessmentName(),
                        notification.getNotificationTime(),
                        notification.getIsRead()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long id) {
        // Ensure the notification exists before attempting to update
        AssessmentNotification notification = assessmentNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Update the notification status to read
        notification.setIsRead(true);
        assessmentNotificationRepository.save(notification);
    }
}
