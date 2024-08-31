package com.ILPex.repository;

import com.ILPex.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository  extends JpaRepository<Holiday, LocalDate> {
    boolean existsByDate(LocalDate date);
}
