package com.ILPex.controller;

import com.ILPex.service.PercipioApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/percipio")
public class PercipioController {

    @Autowired
    private PercipioApiService percipioApiService;

    @GetMapping("/generate-request-id")
    public String generateRequestId() {
        return percipioApiService.generateRequestId();
    }

    @GetMapping("/fetch-data/{requestId}")
    public String fetchData(@PathVariable String requestId) {
        return percipioApiService.fetchData(requestId);
    }
}
