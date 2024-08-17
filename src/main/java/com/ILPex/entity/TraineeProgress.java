package com.ILPex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
}
