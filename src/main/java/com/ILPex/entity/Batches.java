package com.ILPex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "batches")
public class Batches extends BaseEntity{

    @Column(name="batch_name")
    private String batchName;

    @Column(name="start_date")
    private Timestamp startDate;

    @Column(name="end_date")
    private Timestamp endDate;

    @Column(name="is_active")
    private Boolean isActive;

    @Column(name="day_number")
    private Boolean dayNumber;

    @OneToMany(mappedBy = "batches", cascade = CascadeType.ALL,targetEntity = Trainees.class)
    private Set<Trainees> trainees = new HashSet<>();

    @OneToMany(mappedBy = "batches", cascade = CascadeType.ALL,targetEntity = AssessmentBatchAllocation.class)
    private Set<AssessmentBatchAllocation> assessmentBatchAllocations = new HashSet<>();


}

