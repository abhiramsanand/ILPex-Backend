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
@Table(name="assessments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Assessments extends BaseEntity{

    @Column(name="assessment_name",nullable= false,length = 255,columnDefinition = "TEXT")
    private String assessmentName;
    @Column(name="due_date",nullable= false)
    private Timestamp dueDate;
    @Column(name="isActive",nullable= false)
    private Boolean isActive;





}
