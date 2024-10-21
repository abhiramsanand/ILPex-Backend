package com.ILPex.repository;

import com.ILPex.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findByRoles_Id(@Param("roleId") Long roleId);
    Users findByUserName(String userName);
    List<Users> findByRoles_IdAndIsActiveTrue(Long roleId);

}
