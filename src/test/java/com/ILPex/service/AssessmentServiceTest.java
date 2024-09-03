package com.ILPex.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ILPex.DTO.AssessmentResponseDTO;
import com.ILPex.DTO.QuestionsDTO;
import com.ILPex.DTO.TraineeAssessmentDisplayDTO;
import com.ILPex.entity.*;
import com.ILPex.repository.*;
import com.ILPex.service.Impl.AssessmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class AssessmentServiceTest {

    @Mock
    private AssessmentsRepository assessmentRepository;

    @Mock
    private QuestionsRepository questionsRepository;

    @Mock
    private ResultsRepository resultsRepository;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;

    @Mock
    private TraineesRepository traineesRepository;

    @Mock
    private AssessmentBatchAllocationRepository assessmentBatchAllocationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidAssessmentId_whenGetAssessmentById_thenReturnTraineeAssessmentDisplayDTO() {
        // Given
        Long assessmentId = 1L;
        Assessments assessment = new Assessments();
        assessment.setAssessmentName("Math Test");

        Questions question1 = new Questions();
        question1.setQuestion("What is 2 + 2?");
        question1.setOptionA("4"); // Correct option
        question1.setOptionB("3");
        question1.setOptionC("5");
        question1.setOptionD("6");
        question1.setCorrectAnswer("4");
        question1.setAssessments(assessment);

        Questions question2 = new Questions();
        question2.setQuestion("What is 3 + 5?");
        question2.setOptionA("8"); // Correct option
        question2.setOptionB("7");
        question2.setOptionC("9");
        question2.setOptionD("10");
        question2.setCorrectAnswer("8");
        question2.setAssessments(assessment);

        assessment.setQuestions(new HashSet<>(Arrays.asList(question1, question2)));

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        // When
        TraineeAssessmentDisplayDTO result = assessmentService.getAssessmentById(assessmentId);

        // Then
        assertNotNull(result);
        assertEquals("Math Test", result.getAssessmentName());

        List<QuestionsDTO> questions = result.getQuestions();
        assertEquals(2, questions.size());

        QuestionsDTO q1 = questions.get(0);
        assertEquals("What is 2 + 2?", q1.getQuestion());
        assertEquals("4", q1.getOptionA()); // Check if this matches correct answer

        QuestionsDTO q2 = questions.get(1);
        assertEquals("What is 3 + 5?", q2.getQuestion());
        assertEquals("8", q2.getOptionA()); // Check if this matches correct answer
    }


    @Test
    void givenInvalidAssessmentId_whenGetAssessmentById_thenThrowRuntimeException() {
        // Given
        Long assessmentId = 9999L;
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            assessmentService.getAssessmentById(assessmentId);
        });

        assertEquals("Assessment not found", thrown.getMessage());
    }

    @Test
    void givenValidResponseDTO_whenCalculateAssessmentScore_thenReturnCalculatedScore() {
        // Given
        Long assessmentId = 1L;
        String assessmentName = "Math Test";
        Long questionId1 = 1L;
        Long questionId2 = 2L;
        String correctAnswer1 = "4";
        String correctAnswer2 = "8";
        Map<Long, String> questionResponses = new HashMap<>();
        questionResponses.put(questionId1, "4");
        questionResponses.put(questionId2, "8");

        Assessments assessment = new Assessments();
        assessment.setAssessmentName(assessmentName);

        Questions question1 = new Questions();
        question1.setId(questionId1);
        question1.setCorrectAnswer(correctAnswer1);

        Questions question2 = new Questions();
        question2.setId(questionId2);
        question2.setCorrectAnswer(correctAnswer2);

        // Mock repository methods
        when(assessmentRepository.findByAssessmentName(assessmentName)).thenReturn(assessment);
        when(questionsRepository.findById(questionId1)).thenReturn(Optional.of(question1));
        when(questionsRepository.findById(questionId2)).thenReturn(Optional.of(question2));

        // Initialize and set a single batch correctly
        Batches batch = new Batches();
        // Set properties for batch if necessary

        Trainees trainee = new Trainees();
        trainee.setBatches(batch);  // Set single Batches object

        AssessmentBatchAllocation batchAllocation = new AssessmentBatchAllocation();

        // Mock additional repository methods
        when(traineesRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(assessmentBatchAllocationRepository.findByAssessmentsAndBatches(assessment, batch))
                .thenReturn(batchAllocation);

        // ResponseDTO
        AssessmentResponseDTO responseDTO = new AssessmentResponseDTO();
        responseDTO.setAssessmentName(assessmentName);
        responseDTO.setQuestionResponses(questionResponses);
        responseDTO.setTraineeId(1L);

        // When
        int score = assessmentService.calculateAssessmentScore(responseDTO);

        // Then
        assertEquals(100, score); // Both answers are correct, so score should be 100%

        // Verify interactions
        verify(resultsRepository).save(any(Results.class));
    }
    @Test
    void givenNullOrEmptyQuestionResponses_whenCalculateAssessmentScore_thenThrowIllegalArgumentException() {
        // Given
        AssessmentResponseDTO responseDTO = new AssessmentResponseDTO();
        responseDTO.setAssessmentName("Math Test");
        responseDTO.setQuestionResponses(null);  // or use empty map
        responseDTO.setTraineeId(1L);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            assessmentService.calculateAssessmentScore(responseDTO);
        }, "Question responses cannot be null or empty");
    }


}
