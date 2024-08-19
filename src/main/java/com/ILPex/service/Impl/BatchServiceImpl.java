package com.ILPex.service.Impl;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.entity.Batches;
import com.ILPex.repository.BatchRepository;
import com.ILPex.service.BatchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BatchRepository batchRepository;

    @Override
    public List<BatchDTO> getBatches() {
        List<Batches> batchList = batchRepository.findAll();
        return batchList.stream().map(batch -> {
            return modelMapper.map(batch, BatchDTO.class);
        }).collect(Collectors.toList());
    }
}
