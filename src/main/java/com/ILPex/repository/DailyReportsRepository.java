package com.ILPex.repository;

import com.ILPex.entity.DailyReports;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyReportsRepository extends JpaRepository<DailyReports,Long> {
    List<DailyReports> findByTrainees_IdAndDate(Long traineeId, LocalDate date);
    Optional<DailyReports> findById(Long id);
}
