package com.ILPex.repository;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    boolean existsByTraineesAndCourseNameAndCompletionStatus(Trainees trainees, String courseName, String completionStatus);
    @Query("SELECT DISTINCT tp.trainees.id FROM TraineeProgress tp")
    List<Long> findDistinctTraineeIds();

    Optional<TraineeProgress> findTopByTrainees_IdOrderByCompletedDateDesc(Long traineeId);

    @Query("SELECT tp FROM TraineeProgress tp WHERE tp.trainees.id = :traineeId ORDER BY tp.completedDate DESC")
    List<TraineeProgress> findProgressByTraineeId(Long traineeId);
}
