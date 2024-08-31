package com.ILPex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentNotificationDTO {
    private Long id;
    private String assessmentName;
    private LocalDateTime notificationTime;
    private Boolean isRead;
}
