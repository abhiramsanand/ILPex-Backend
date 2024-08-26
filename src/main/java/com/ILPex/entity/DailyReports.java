package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "dailyReports")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyReports extends BaseEntity {

    @Column(name = "date")
    private Date date;

    @Column(name = "time_taken")
    private Integer timeTaken; // Duration is preferred for time taken

    @Column(name = "key_learnings")
    private String keylearnings;

    @Column(name = "plan_for_tomorrow")
    private String planfortomorrow;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("daily_reports")
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    private Trainees trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("daily_reports")
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Courses courses;
}
