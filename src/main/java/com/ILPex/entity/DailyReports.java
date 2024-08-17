package com.ILPex.entity;

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

    @Column(name = "date", nullable = false)
    private LocalDate date;


    @Column(name = "time_taken", nullable = false)
    private LocalDateTime timeTaken;

    @Column(name = "key_learnings", nullable = false, columnDefinition = "TEXT")
    private String keyLearnings;

    @Column(name = "plan_for_tomorrow", nullable = false, columnDefinition = "TEXT")
    private String planForTomorrow;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;
    public enum ReportStatus {
        SUBMITTED,
        PENDING
    }
}
