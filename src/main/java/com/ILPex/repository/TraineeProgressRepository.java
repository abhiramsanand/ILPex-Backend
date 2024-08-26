package com.ILPex.repository;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.entity.TraineeProgress;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeProgressRepository extends JpaRepository<TraineeProgress, Long> {

    @Query("SELECT new com.ILPex.DTO.CourseProgressDTO(" +
            "c.courseName, " +
            "c.dayNumber, " +
            "c.courseDuration, " +
            "tp.duration, " +
            "(CASE WHEN tp.estimatedDuration > 0 THEN " +
            "ROUND((tp.duration / tp.estimatedDuration) * 100, 2) " +
            "ELSE 0 END)) " +
            "FROM TraineeProgress tp " +
            "JOIN tp.courses c " +
            "WHERE tp.trainees.id = :traineeId")
    List<CourseProgressDTO> findCourseProgressByTraineeId(@Param("traineeId") Long traineeId);


}
