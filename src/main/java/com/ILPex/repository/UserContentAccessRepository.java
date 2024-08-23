package com.ILPex.repository;

import com.ILPex.entity.UserContentAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserContentAccessRepository extends JpaRepository<UserContentAccess, Long> {
}
