package com.ILPex.repository;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import java.util.Optional;

@Repository
public interface CoursesRepository extends JpaRepository<Courses,Long> {
    Optional<Courses> findByCourseName(String courseName);

    List<Courses> findByCourseDateAndBatchId(LocalDateTime courseDate, Long batchId);


    @Query("SELECT new com.ILPex.DTO.CourseDayBatchDTO(c.courseName, c.dayNumber, b.batchName, c.courseDuration) " +
            "FROM Courses c " +
            "JOIN c.batch b " +
            "WHERE b.id = :batchId " +
            "ORDER BY c.dayNumber")
    List<CourseDayBatchDTO> findCoursesByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT c.dayNumber FROM Courses c WHERE c.courseName = :courseName")
    Optional<Integer> findDayNumberByCourseName(String courseName);

    @Query("SELECT c.dayNumber FROM Courses c WHERE c.id = :courseId")
    Optional<Integer> findDayNumberByCourseId(Long courseId);

    @Query("SELECT SUM(FUNCTION('parse_duration_to_minutes', c.courseDuration)) " +
            "FROM Courses c " +
            "WHERE c.batch.id = :batchId " +
            "AND c.courseDate <= CURRENT_DATE")
    Long findTotalCourseDurationByBatchId(@Param("batchId") Long batchId);

    @Query("SELECT COUNT(DISTINCT c.dayNumber) FROM Courses c WHERE c.batch.id = :batchId AND c.courseDate <= CURRENT_DATE")
    Long countDistinctCourseDaysCompletedByBatchId(@Param("batchId") Long batchId);

    @Query(value = "SELECT SUM(" +
            "CASE " +
            "WHEN course_duration ~ '^[0-9]+h [0-9]+m$' THEN " +
            "CAST(SPLIT_PART(course_duration, 'h', 1) AS INTEGER) * 60 + " +
            "CAST(SPLIT_PART(SPLIT_PART(course_duration, 'h', 2), 'm', 1) AS INTEGER) " +
            "WHEN course_duration ~ '^[0-9]+m$' THEN " +
            "CAST(SPLIT_PART(course_duration, 'm', 1) AS INTEGER) " +
            "ELSE 0 " +
            "END " +
            ") AS total_duration_minutes " +
            "FROM courses " +
            "WHERE day_number <= :dayNumber", nativeQuery = true)
    Long getTotalCourseDurationUpToDayNumber(@Param("dayNumber") Integer dayNumber);

    @Query("SELECT c FROM Courses c WHERE c.batch.id = :batchId AND c.courseDate < :currentDate")                       //to display wholereport
    List<Courses> findByBatchIdAndCourseDateBefore(@Param("batchId") Long batchId, @Param("currentDate") Date currentDate);

    @Query("SELECT c FROM Courses c WHERE c.batch.id = :batchId AND c.courseDate < CURRENT_DATE AND c.id NOT IN " +     //to display pending submission
            "(SELECT dr.courses.id FROM DailyReports dr WHERE dr.trainees.id = :traineeId)")
    List<Courses> findPendingSubmissions(Long batchId, Long traineeId);

}

