package com.ILPex.repository;

import com.ILPex.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findByRoles_Id(@Param("roleId") Long roleId);

}
