package com.ILPex.repository;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Batches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batches, Long> {


    @Query("SELECT new com.ILPex.DTO.CourseDayBatchDTO(c.courseName, c.dayNumber, b.batchName, c.courseDuration) " +
            "FROM Batches b " +
            "JOIN b.trainees t " +
            "JOIN t.traineeProgresses tp " +
            "JOIN tp.courses c " +
            "ORDER BY b.id, c.dayNumber")
    List<CourseDayBatchDTO> findDaywiseCoursesForAllBatches();

    @Query("SELECT b FROM Batches b JOIN b.trainees t WHERE t.id = :traineeId")
    Optional<Batches> findByTrainees_Id(Long traineeId);

    @Query("SELECT b.dayNumber FROM Batches b WHERE b.id = :batchId")
    Integer findDayNumberById(@Param("batchId") Long batchId);
}
