package com.ILPex.repository;

import com.ILPex.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HolidayRepository  extends JpaRepository<Holiday, LocalDate> {
    boolean existsByDate(LocalDate date);
}
