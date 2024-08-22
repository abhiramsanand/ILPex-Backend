package com.ILPex.entity;

import com.ILPex.DTO.DailyReportAddDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dailyReports")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyReports extends BaseEntity {

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time_taken")
    private LocalDateTime timeTaken;

    @Column(name = "key_learnings")
    private String keyLearnings;

    @Column(name = "plan_for_tomorrow")
    private String planForTomorrow;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status;


    public enum ReportStatus {
        SUBMITTED,
        PENDING
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("daily_reports")
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    Trainees trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("daily_reports")
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    Courses courses;
}
