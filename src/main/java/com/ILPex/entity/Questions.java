package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="questions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Questions extends BaseEntity{

    @Column(name="question",nullable= false,length = 255,columnDefinition = "TEXT")
    private String question;

    @Column(name="option_a",nullable= false,length = 255,columnDefinition = "TEXT")
    private String optionA;

    @Column(name="option_b",nullable= false,length = 255,columnDefinition = "TEXT")
    private String optionB;

    @Column(name="option_c",nullable= false,length = 255,columnDefinition = "TEXT")
    private String optionC;

    @Column(name="option_d",nullable= false,length = 255,columnDefinition = "TEXT")
    private String optionD;

    @Column(name="correct_answer",nullable= false,length = 255,columnDefinition = "TEXT")
    private String correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("questions")
    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    Assessments assessments;

}
