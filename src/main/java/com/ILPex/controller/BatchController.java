package com.ILPex.controller;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batches")
@CrossOrigin(origins = "http://localhost:5173")
public class BatchController {
    @Autowired
    private BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchDTO>> getBatches() {
        List<BatchDTO> batchList = batchService.getBatches();
        return ResponseEntity.ok(batchList);
    }
}
