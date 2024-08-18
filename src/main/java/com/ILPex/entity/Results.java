package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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
public class Results extends BaseEntity {

    @Column(name = "assessment_batches_allocation_id", nullable = false, insertable = false, updatable = false)
    private int assessmentBatchesAllocationId;

    @Column(name = "trainee_id", nullable = false, insertable = false, updatable = false)
    private int traineeId;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "assessment_attempts", nullable = false)
    private int assessmentAttempts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("results")
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    private Trainees trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("results")
    @JoinColumn(name = "assessment_batches_allocation_id", referencedColumnName = "id", nullable = false)
    private AssessmentBatchAllocation assessmentBatchAllocation;
}
