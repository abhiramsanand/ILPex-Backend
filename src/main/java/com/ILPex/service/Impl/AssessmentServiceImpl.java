package com.ILPex.service.Impl;

import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.repository.AssessmentsRepository;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService{

    @Autowired
    private AssessmentsRepository assessmentRepository;


    @Override
    public Page<AssessmentReportDTO> getAssessmentDetailsByBatchIdAndStatus(Long batchId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("assessmentName").ascending());
        Page<Object[]> resultPage = assessmentRepository.getAssessmentDetailsByBatchIdAndStatus(batchId, status, pageable);

        return resultPage.map(result -> new AssessmentReportDTO(
                (String) result[0], // assessmentName
                (Boolean) result[1], // assessmentStatus
                (String) result[2], // batchName
                (Long) result[3], // numberOfStudentsAttended
                (String) result[4], // traineeName
                (String) result[5], // traineeStatus
                (Integer) result[6] // score
        ));
    }

}
