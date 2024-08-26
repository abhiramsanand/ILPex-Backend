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
}

