package com.ILPex.service.Impl;

import com.ILPex.DTO.PendingCourseDTO;
import com.ILPex.entity.Courses;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.service.PendingCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PendingCoursesServiceImpl implements PendingCourseService {

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private TraineeProgressRepository traineeProgressRepository;

    @Override
    public List<PendingCourseDTO> getPendingCoursesForToday(Long traineeId) {
        // Query logic based on the provided SQL
        return coursesRepository.findAll().stream()
                .filter(course -> course.getDayNumber() == LocalDate.now().getDayOfMonth())
                .map(course -> {
                    TraineeProgress progress = traineeProgressRepository.findByCourseNameAndTraineesId(course.getCourseName(), traineeId).orElse(null);

                    // Check if the progress exists and the actual duration is less than or equal to the estimated duration
                    if (progress != null && progress.getDuration() <= progress.getEstimatedDuration()) {
                        return new PendingCourseDTO(
                                course.getCourseName(),
                                course.getDayNumber()
                        );
                    }
                    return null;
                })
                .filter(courseDTO -> courseDTO != null)
                .sorted((a, b) -> {
                    if (a.getDayNumber() != b.getDayNumber()) {
                        return Integer.compare(a.getDayNumber(), b.getDayNumber());
                    }
                    return a.getCourseName().compareTo(b.getCourseName());
                })
                .collect(Collectors.toList());
    }
}
