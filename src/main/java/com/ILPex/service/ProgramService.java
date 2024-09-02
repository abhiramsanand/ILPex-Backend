package com.ILPex.service;

import com.ILPex.DTO.ProgramDTO;
import com.ILPex.entity.Programs;

import java.util.List;

public interface ProgramService {
    Programs findByProgramName(String programName);
    List<ProgramDTO> getAllPrograms();
    void createProgram(ProgramDTO programDTO);
}
