package com.ILPex.repository;

import com.ILPex.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CoursesRepository extends JpaRepository<Courses,Long> {
    List<Courses> findByCourseDateAndBatchId(LocalDateTime courseDate, Long batchId);

}
