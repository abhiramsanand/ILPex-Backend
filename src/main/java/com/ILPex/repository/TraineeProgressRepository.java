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


    @Query(value = "SELECT " +
            "    t.course_name AS courseName, " +
            "    c.day_number AS dayNumber, " +
            "    t.estimated_duration AS estimatedDuration, " +
            "    t.duration AS duration " +
            "FROM " +
            "    trainee_progress t " +
            "JOIN " +
            "    courses c " +
            "    ON c.course_name = t.course_name " +
            "WHERE " +
            "    t.trainee_id = :traineeId " +
            "ORDER BY " +
            "    c.day_number, c.course_name",
            nativeQuery = true)
    List<Object[]> findCourseProgressByTraineeId(@Param("traineeId") Long traineeId);

}