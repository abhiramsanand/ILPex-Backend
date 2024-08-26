package com.ILPex.repository;
import com.ILPex.DTO.TraineeCompletedAssessmentDTO;
import com.ILPex.entity.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ResultsRepository extends JpaRepository<Results,Integer> {


    @Query("SELECT new com.ILPex.DTO.TraineeCompletedAssessmentDTO(a.assessmentName, r.score, r.traineeId) " +
            "FROM Assessments a " +
            "JOIN a.assessmentBatchAllocations aba ON a.id = aba.assessments.id " +
            "JOIN aba.results r ON aba.id = r.assessmentBatchAllocation.id " +
            "WHERE r.traineeId = :traineeId AND r.score IS NOT NULL")
    List<TraineeCompletedAssessmentDTO> findCompletedAssessmentsByTraineeId(int traineeId);

    @Query("SELECT DISTINCT r.traineeId FROM Results r")
    List<Long> findDistinctTraineeIds();

    @Query("SELECT AVG(r.score) FROM Results r WHERE r.traineeId = :traineeId")
    Double findAverageScoreByTraineeId(Long traineeId);
}
