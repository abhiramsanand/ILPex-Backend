package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {


    @Autowired
    private CoursesRepository coursesRepository;


    @Override
    public List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId) {
        return coursesRepository.findCoursesByBatchId(batchId);
    }
}