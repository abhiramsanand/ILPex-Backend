package com.ILPex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class Users extends BaseEntity{

    @Column(name="username")
    private String userName;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="last_access")
    private Timestamp lastAccess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("users")
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    Roles roles;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL,targetEntity = Trainees.class)
    private Set<Trainees> trainees = new HashSet<>();
}
