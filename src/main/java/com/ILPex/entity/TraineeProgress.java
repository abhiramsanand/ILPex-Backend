package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "trainee_Progress")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TraineeProgress extends BaseEntity {

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(name = "completion_status", nullable = false)
    private String completionStatus;

    @Column(name="course_name")
    private String courseName;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "estimated_duration", nullable = false)
    private int estimatedDuration;

    @Column(name = "completed_date", nullable = false)
    private Timestamp completedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("trainee_progress")
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    Trainees trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("trainee_progress")
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    Courses courses;
}
