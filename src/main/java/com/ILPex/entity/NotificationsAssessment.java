package com.ILPex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "notifications_assessment")
public class NotificationsAssessment extends BaseEntity{

    @Column(name = "message")
    private String message;

    @Enumerated
    (EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status;
    public enum NotificationStatus { SENT, READ, PENDING }
}
