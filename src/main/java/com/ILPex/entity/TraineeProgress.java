package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "traineeProgress")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TraineeProgress extends BaseEntity {

    @Column(name = "day_number", nullable = false)
    private int dayNumber;


    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "estimated_duration", nullable = false)
    private int estimatedDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("trainee_progress")
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    Trainees trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("trainee_progress")
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    Courses courses;
}
