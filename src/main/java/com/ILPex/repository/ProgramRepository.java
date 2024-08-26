package com.ILPex.repository;

import com.ILPex.entity.Programs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Programs, Long> {
    Programs findByProgramName(String programName);
}
