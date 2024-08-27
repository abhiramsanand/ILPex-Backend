package com.ILPex.repository;


import com.ILPex.DTO.TraineeDTO;
import com.ILPex.entity.Trainees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface TraineesRepository extends JpaRepository<Trainees, Long> {
    List<Trainees> findByBatchesId(Long batchId);
    Trainees findByPercipioEmail(String percipioEmail);

    @Query("SELECT new com.ILPex.DTO.TraineeDTO(t.id, u.userName) " +
            "FROM Trainees t JOIN t.users u WHERE t.batches.id = :batchId")
    List<TraineeDTO> findTraineesByBatchId(@Param("batchId") Long batchId);


}
