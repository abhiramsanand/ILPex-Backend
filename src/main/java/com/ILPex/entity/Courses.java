package com.ILPex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "courses")
public class Courses extends BaseEntity{

    @Column(name = "day_number")
    private int dayNumber;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_type")
    private String courseType;

    @Column(name = "course_duration")
    private String courseDuration;


    @OneToMany(mappedBy = "courses", cascade = CascadeType.ALL,targetEntity = PercipioAssessment.class)
    private Set<PercipioAssessment> percipioAssessments = new HashSet<>();

    @OneToMany(mappedBy = "courses", cascade = CascadeType.ALL,targetEntity = TraineeProgress.class)
    private Set<TraineeProgress> traineeProgresses = new HashSet<>();

    @OneToMany(mappedBy = "courses", cascade = CascadeType.ALL,targetEntity = DailyReports.class)
    private Set<DailyReports>dailyReports = new HashSet<>();

}
