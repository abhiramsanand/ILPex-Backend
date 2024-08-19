package com.ILPex.repository;

import com.ILPex.entity.Batches;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batches, Long> {
}
