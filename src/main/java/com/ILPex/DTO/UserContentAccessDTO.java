package com.ILPex.DTO;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserContentAccessDTO {

    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String audience;
    private String contentUuid;
    private String contentTitle;
    private String contentType;
    private String languageCode;
    private String category;
    private String source;
    private String status;
    private Timestamp completedDate;
    private int duration;
    private int estimatedDuration;
    private Timestamp firstAccess;
    private Timestamp lastAccess;
    private int totalAccesses;
    private String emailAddress;
    private String durationHms;
    private String estimatedDurationHms;
    private String userUuid;
    private String userStatus;
    private  int highScore;
}
