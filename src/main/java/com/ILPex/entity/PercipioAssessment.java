package com.ILPex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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


}
