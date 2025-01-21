package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Transactional
@Table(name = "batches")
public class Batches extends BaseEntity {

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "day_number")
    private Long dayNumber;

    @OneToMany(mappedBy = "batches", cascade = CascadeType.ALL, targetEntity = Trainees.class, fetch =  FetchType.EAGER)
    @JsonIgnore // Prevent serialization
    private Set<Trainees> trainees = new HashSet<>();

    @OneToMany(mappedBy = "batches", cascade = CascadeType.ALL, targetEntity = AssessmentBatchAllocation.class,fetch =  FetchType.EAGER)
    @JsonIgnore // Prevent serialization
    private Set<AssessmentBatchAllocation> assessmentBatchAllocations = new HashSet<>();

    @ManyToOne(fetch =  FetchType.EAGER)
    @JsonIgnoreProperties("programs")
    @JoinColumn(name = "program_id", referencedColumnName = "id", nullable = false)
    private Programs programs;
}
