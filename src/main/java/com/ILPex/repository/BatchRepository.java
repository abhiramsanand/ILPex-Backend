package com.ILPex.repository;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Batches;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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


}
