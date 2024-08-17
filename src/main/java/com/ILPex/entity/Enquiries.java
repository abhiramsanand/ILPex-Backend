package com.ILPex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enquires")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Enquiries extends BaseEntity{

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "send_date", nullable = false)
    private LocalDateTime sendDate;


}
