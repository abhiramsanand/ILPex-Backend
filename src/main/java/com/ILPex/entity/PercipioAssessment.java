package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="percipio_assessment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PercipioAssessment extends BaseEntity {

    @Column(name="day_number",nullable= false)
    private int dayNumber;

    @Column(name="score",nullable= false)
    private Long score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("percipio_assessment")
    @JoinColumn(name = "trainee_id", referencedColumnName = "id", nullable = false)
    Trainees trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("percipio_assessment")
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    Courses courses;
}
