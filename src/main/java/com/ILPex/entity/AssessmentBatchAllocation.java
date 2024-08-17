package com.ILPex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name="assessment_batch_allocation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssessmentBatchAllocation extends BaseEntity{
    @Column(name="start_date",nullable= false)
    private Timestamp startDate;
    @Column(name="end_date",nullable= false)
    private Timestamp endDate;
    @Column(name="assessment_status",nullable= false)
    private Boolean assessmentStatus;





}
