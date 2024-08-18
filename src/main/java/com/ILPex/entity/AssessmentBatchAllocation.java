package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "assessment_batch_allocation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssessmentBatchAllocation extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @Column(name = "end_date", nullable = false)
    private Timestamp endDate;

    @Column(name = "assessment_status", nullable = false)
    private Boolean assessmentStatus;

    @OneToMany(mappedBy = "assessmentBatchAllocation", cascade = CascadeType.ALL, targetEntity = Results.class)
    private Set<Results> results = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("assessment_batch_allocation")
    @JoinColumn(name = "batch_id", referencedColumnName = "id", nullable = false)
    Batches batches;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("assessment_batch_allocation")
    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    Assessments assessments;
}

