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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "programs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "batches"})
public class Programs extends BaseEntity {

    @Column(name = "program_name")
    private String programName;

    @OneToMany(mappedBy = "programs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("programs") // Ignore 'programs' property in Batches to break the cycle
    private Set<Batches> batches = new HashSet<>();
}
