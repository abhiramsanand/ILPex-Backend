package com.ILPex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "result")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Results extends BaseEntity{
    @Column(name = "assessment_batches_allocation_id", nullable = false)
    private int assessmentBatchesAllocationId;

    @Column(name = "trainee_id", nullable = false)
    private int traineeId;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "assessment_attempts", nullable = false)
    private int assessmentAttempts;
}
