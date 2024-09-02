package com.ILPex.service.Impl;

import com.ILPex.DTO.ProgramDTO;
import com.ILPex.entity.Programs;
import com.ILPex.repository.ProgramRepository;
import com.ILPex.service.ProgramService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramServiceImpl implements ProgramService {

    @Autowired
    private ProgramRepository programRepository;


    @Autowired
    private ModelMapper modelMapper;

    public Programs findByProgramName(String programName) {
        return programRepository.findByProgramName(programName);
    }
    public List<ProgramDTO> getAllPrograms() {
        List<Programs> programs = programRepository.findAll();
        return programs.stream()
                .map(program -> modelMapper.map(program, ProgramDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public void createProgram(ProgramDTO programDTO) {
        Programs program = modelMapper.map(programDTO, Programs.class);
        programRepository.save(program);
    }
}