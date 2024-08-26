package com.ILPex.repository;

import com.ILPex.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import java.util.Optional;

public interface CoursesRepository extends JpaRepository<Courses,Long> {
    Optional<Courses> findByCourseName(String courseName);
    List<Courses> findByCourseDateAndBatchId(LocalDateTime courseDate, Long batchId);

}
