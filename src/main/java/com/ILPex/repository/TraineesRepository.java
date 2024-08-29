package com.ILPex.repository;


import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDailyReportDTO;
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

    @Query("SELECT new com.ILPex.DTO.TraineeDailyReportDTO(t.users.userName, " +
            "(SELECT COUNT(c.id) FROM Courses c WHERE c.batch.id = b.id AND c.courseDate < CURRENT_DATE), " +
            "(SELECT COUNT(d.id) FROM DailyReports d WHERE d.trainees.batches.id = b.id AND d.trainees.users.id = t.users.id)) " +
            "FROM Trainees t " +
            "JOIN t.batches b " +
            "WHERE b.id = :batchId")
    List<TraineeDailyReportDTO> findTraineeReportsByBatchId(@Param("batchId") Long batchId);

}
