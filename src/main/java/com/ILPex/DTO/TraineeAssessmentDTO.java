package com.ILPex.DTO;

import java.security.Timestamp;

public class TraineeAssessmentDTO {
        private int assessmentId;
        private String assessmentName;
        private Integer score;
        private Timestamp dueDate;
        private String completionStatus;

        public TraineeAssessmentDTO(int assessmentId, String assessmentName, Integer score, Timestamp dueDate, Timestamp dateTaken) {
            this.assessmentId = assessmentId;
            this.assessmentName = assessmentName;
            this.score = score;
            this.dueDate = dueDate;
            this.completionStatus = (score == null) ? "Pending" : "Completed";
        }

        // Getters and setters
    }

