package com.ILPex.repository;

import com.ILPex.entity.AssessmentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentNotificationRepository extends JpaRepository<AssessmentNotification, Long> {
    List<AssessmentNotification> findByTraineeId(Long traineeId);

    @Modifying
    @Query("UPDATE AssessmentNotification an SET an.isRead = true WHERE an.id = :id")
    void markAsRead(Long id);

}
