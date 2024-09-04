package com.ILPex.repository;


import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDailyReportDTO;
import com.ILPex.DTO.TraineeDayProgressDTO;
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

    @Query("SELECT new com.ILPex.DTO.TraineeDailyReportDTO(t.id,t.users.userName, " +
            "(SELECT COUNT(c.id) FROM Courses c WHERE c.batch.id = b.id AND c.courseDate < CURRENT_DATE), " +
            "(SELECT COUNT(d.id) FROM DailyReports d WHERE d.trainees.batches.id = b.id AND d.trainees.users.id = t.users.id)) " +
            "FROM Trainees t " +
            "JOIN t.batches b " +
            "WHERE b.id = :batchId")
    List<TraineeDailyReportDTO> findTraineeReportsByBatchId(@Param("batchId") Long batchId);


    //trainee current day
    @Query(value = "SELECT " +
            "u.username AS trainee_name, " +
            "MAX(c.day_number) AS last_day_number " +
            "FROM public.trainees t " +
            "JOIN public.users u ON t.user_id = u.id " +
            "JOIN public.trainee_progress tp ON t.id = tp.trainee_id " +
            "JOIN public.courses c ON tp.course_name = c.course_name " +
            "WHERE t.batch_id = :batchId " +
            "GROUP BY u.username " +
            "ORDER BY u.username",
            nativeQuery = true)
    List<Object[]> findLastDayForTraineesInBatch(@Param("batchId") Long batchId);
}
