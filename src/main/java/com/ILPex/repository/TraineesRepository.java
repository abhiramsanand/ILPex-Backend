package com.ILPex.repository;

import com.ILPex.entity.Trainees;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TraineesRepository extends JpaRepository<Trainees, Long> {
    List<Trainees> findByBatchesId(Long batchId);
    Trainees findByPercipioEmail(String percipioEmail);
}
