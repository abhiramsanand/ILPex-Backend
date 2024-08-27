package com.ILPex.repository;

import com.ILPex.entity.DailyReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DailyReportsRepository extends JpaRepository<DailyReports, Long> {
    @Query("SELECT dr FROM DailyReports dr WHERE dr.courses.id = :courseId AND dr.trainees.id = :traineeId")
    Optional<DailyReports> findByCourseIdAndTraineeId(Long courseId, Long traineeId);
    Optional<DailyReports> findByTrainees_IdAndCourses_Id(Long traineeId, Long courseId);
    @Query("SELECT dr FROM DailyReports dr WHERE dr.trainees.id = :traineeId ORDER BY dr.date DESC")
    List<DailyReports> findDailyReportsByTraineeId(Long traineeId);


    //    Optional<DailyReports> findById(Long id);
}
