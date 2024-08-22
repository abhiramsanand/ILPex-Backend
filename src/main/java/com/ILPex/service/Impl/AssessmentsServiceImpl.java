package com.ILPex.service.Impl;

import com.ILPex.DTO.AssessmentDetailsDTO;
import com.ILPex.repository.AssessmentsRepository;
import com.ILPex.service.AssessmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AssessmentsServiceImpl implements AssessmentsService {
    private final AssessmentsRepository assessmentsRepository;

    @Autowired
    public AssessmentsServiceImpl(AssessmentsRepository assessmentsRepository) {
        this.assessmentsRepository = assessmentsRepository;
    }

    @Override
    public List<AssessmentDetailsDTO> getAssessmentDetails() {
        return assessmentsRepository.fetchAssessmentDetails();
    }


    @Override
    public List<AssessmentDetailsDTO> getAssessmentDetailsByBatchId(Long batchId) {
        return assessmentsRepository.fetchAssessmentDetailsByBatchId(batchId);
    }
}
