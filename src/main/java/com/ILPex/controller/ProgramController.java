package com.ILPex.controller;

import com.ILPex.DTO.ProgramDTO;
import com.ILPex.entity.Programs;
import com.ILPex.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/v1/programs")
@CrossOrigin(origins = "http://localhost:5173")
public class ProgramController {

    @Autowired
    private ProgramService programService;


    @GetMapping
    public ResponseEntity<List<ProgramDTO>> getAllPrograms() {
        List<ProgramDTO> programs = programService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }
}
