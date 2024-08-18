package com.ILPex.entity;

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
@Table(name = "roles")
public class Roles extends BaseEntity{

    @Column(name="role_name")
    private String roleName;

    @OneToMany(mappedBy = "roles", cascade = CascadeType.ALL,targetEntity = Users.class)
    private Set<Users> users = new HashSet<>();
}
