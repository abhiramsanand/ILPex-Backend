package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "trainees")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Trainees  extends BaseEntity{

    @Column(name="percipio_email")
    private String percipioEmail;

    @Column(name="user_uuid")
    private UUID userUuid;

    @Column(name="isActive")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("trainees")
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("trainees")
    @JoinColumn(name = "batch_id", referencedColumnName = "id", nullable = false)
    Batches batches;



    @OneToMany(mappedBy = "trainees", cascade = CascadeType.ALL,targetEntity = PercipioAssessment.class)
    private Set<PercipioAssessment> percipioAssessments = new HashSet<>();

    @OneToMany(mappedBy = "trainees", cascade = CascadeType.ALL,targetEntity = Results.class)
    private Set<Results> results = new HashSet<>();

    @OneToMany(mappedBy = "trainees", cascade = CascadeType.ALL,targetEntity = TraineeProgress.class)
    private Set<TraineeProgress> traineeProgresses = new HashSet<>();

    @OneToMany(mappedBy = "trainees", cascade = CascadeType.ALL,targetEntity = DailyReports.class)
    private Set<DailyReports> dailyReports = new HashSet<>();





}

