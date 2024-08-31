package com.ILPex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "assessment_notifications")
public class AssessmentNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_batch_allocation_id", referencedColumnName = "id", nullable = false)
    private AssessmentBatchAllocation assessmentBatchAllocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    private Trainees trainee;

    @Column(name = "notification_time", nullable = false)
    private LocalDateTime notificationTime = LocalDateTime.now();

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
}
