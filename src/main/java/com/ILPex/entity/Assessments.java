package com.ILPex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="assessments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Assessments extends BaseEntity{

    @Column(name="assessment_name")
    private String assessmentName;

    @Column(name="due_date")
    private Timestamp dueDate;

    @Column(name="isActive")
    private Boolean isActive;

    @OneToMany(mappedBy = "assessments", cascade = CascadeType.ALL,targetEntity = Questions.class)
    private Set<Questions> questions = new HashSet<>();

    @OneToMany(mappedBy = "assessments", cascade = CascadeType.ALL,targetEntity = AssessmentBatchAllocation.class)
    private Set<AssessmentBatchAllocation> assessmentBatchAllocations = new HashSet<>();
}
