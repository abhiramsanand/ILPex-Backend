package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="programs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Programs extends BaseEntity {
    @Column(name = "program_name", nullable = false)
    private String programName;

    @OneToMany(mappedBy = "programs", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Batches.class)
    private Set<Batches> batches = new HashSet<>();


}
