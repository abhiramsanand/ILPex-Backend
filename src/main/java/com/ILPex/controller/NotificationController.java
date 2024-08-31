package com.ILPex.controller;

import com.ILPex.DTO.AssessmentNotificationDTO;
import com.ILPex.service.AssessmentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private AssessmentNotificationService assessmentNotificationService;

    @GetMapping("/trainee/{traineeId}")
    public ResponseEntity<List<AssessmentNotificationDTO>> getNotificationsForTrainee(@PathVariable Long traineeId) {
        List<AssessmentNotificationDTO> notifications = assessmentNotificationService.getNotificationsForTrainee(traineeId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        assessmentNotificationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }
}

