package com.ILPex.repository;

import com.ILPex.DTO.*;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface TraineeProgressRepository extends JpaRepository<TraineeProgress, Long> {


    @Query(value = "SELECT " +
            "    c.course_name AS courseName, " +
            "    c.day_number AS dayNumber, " +
            "    tp.completion_status, " +
            "    COALESCE(tp.estimated_duration, 0) AS estimatedDuration, " +
            "    COALESCE(tp.duration, 0) AS actualDuration " +
            "FROM " +
            "    public.courses c " +
            "LEFT JOIN " +
            "    public.trainee_progress tp " +
            "    ON c.course_name = tp.course_name " +
            "    AND tp.trainee_id = :traineeId " +  // Use a parameterized query
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

    @Query(value = "SELECT u.username, SUM(COALESCE(tp.duration, 0)) " +
            "FROM trainee_progress tp " +
            "JOIN trainees t ON tp.trainee_id = t.id " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN batches b ON b.id = t.batch_id " +
            "WHERE b.id = :batchId " +
            "GROUP BY u.username", nativeQuery = true)
    List<Object[]> findTotalCourseDurationByBatchId(@Param("batchId") Long batchId);

    @Query(value = "SELECT u.username AS trainee_name, COUNT(DISTINCT tp.course_name) AS distinct_course_duration_count " +
            "FROM trainee_progress tp " +
            "JOIN trainees t ON tp.trainee_id = t.id " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN batches b ON b.id = t.batch_id " +
            "WHERE b.id = :batchId " +
            "GROUP BY u.username", nativeQuery = true)
    List<Object[]> getDistinctCourseDurationCountByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT new com.ILPex.DTO.TraineeActualVsEstimatedDurationDTO( u.userName, SUM(tp.duration), SUM(tp.estimatedDuration)) " +
            "FROM TraineeProgress tp JOIN tp.trainees t JOIN t.users u " +
            "WHERE t.batches.id = :batchId " +
            "GROUP BY u.userName")
    List<TraineeActualVsEstimatedDurationDTO> findTotalDurationAndEstimatedDurationByBatchId(@Param("batchId") Long batchId);


    @Query(value = "SELECT COALESCE(MAX(c.day_number), 0) AS last_day_number " +
            "FROM public.trainee_progress tp " +
            "JOIN public.courses c ON tp.course_name = c.course_name " +
            "WHERE tp.trainee_id = :traineeId",
            nativeQuery = true)
    Integer findMaxDayNumberByTraineeId(@Param("traineeId") Long traineeId);
    
    @Query("SELECT new com.ILPex.DTO.TraineeProgressDTO(c.dayNumber, tp.courseName, tp.duration, tp.estimatedDuration) " +
            "FROM TraineeProgress tp JOIN Courses c ON tp.courseName = c.courseName " +
            "WHERE c.courseDate = :courseDate AND tp.trainees.id = :traineeId")
    List<TraineeProgressDTO> findTraineeProgressByCourseDateAndTraineeId(@Param("courseDate") Timestamp courseDate,
                                                                         @Param("traineeId") Long traineeId);
}
