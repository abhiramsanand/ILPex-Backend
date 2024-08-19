package com.ILPex.service;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;

import java.util.List;

public interface BatchService {
    List<BatchDTO> getBatches();
    List<CourseDayBatchDTO> getDaywiseCoursesForAllBatches();
}
