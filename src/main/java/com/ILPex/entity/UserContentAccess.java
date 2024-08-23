package com.ILPex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user_content_access")
public class UserContentAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "audience", nullable = false)
    private String audience;

    @Column(name = "content_uuid", nullable = false)
    private UUID contentUuid;

    @Column(name = "content_title", nullable = false)
    private String contentTitle;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "completed_date")
    private Timestamp completedDate;

    @Column(name = "duration")
    private int duration;

    @Column(name = "estimated_duration")
    private int estimatedDuration;

    @Column(name = "first_access")
    private Timestamp firstAccess;

    @Column(name = "last_access")
    private Timestamp lastAccess;

    @Column(name = "total_accesses")
    private int totalAccesses;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "duration_hms")
    private String durationHms;

    @Column(name = "estimated_duration_hms")
    private String estimatedDurationHms;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "user_status", nullable = false)
    private String userStatus;
}
