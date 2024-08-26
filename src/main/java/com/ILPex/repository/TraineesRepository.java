package com.ILPex.repository;

import com.ILPex.entity.Batches;
import com.ILPex.entity.Trainees;
import com.ILPex.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TraineesRepository extends JpaRepository<Trainees, Long> {
    List<Trainees> findByBatchesId(Long batchId);


}
