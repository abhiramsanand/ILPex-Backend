package com.ILPex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "trainees")
public class Trainees {

    @Column(name="percipio_email")
    private String percipioEmail;

    @Column(name="user_uuid")
    private UUID userUuid;

    @Column(name="isActive")
    private Boolean isActive;

}
