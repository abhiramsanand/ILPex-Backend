package com.ILPex.service.Impl;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.entity.PercipioAssessment;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.PercipioAssessmentRepository;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.PercipioApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PercipioApiServiceImpl implements PercipioApiService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiUrl2;
    private final String organizationUuid;
    private final String jwtToken;
    private final TraineesRepository traineesRepository;
    private final TraineeProgressRepository traineeProgressRepository;
    private final PercipioAssessmentRepository percipioAssessmentRepository;

    public PercipioApiServiceImpl(RestTemplate restTemplate,
                                  @Value("${percipio.api.url}") String apiUrl,
                                  @Value("${percipio.api.url2}") String apiUrl2,
                                  @Value("${percipio.organization.uuid}") String organizationUuid,
                                  @Value("${percipio.api.jwt}") String jwtToken,
                                  TraineesRepository traineesRepository,
                                  TraineeProgressRepository traineeProgressRepository,
                                  PercipioAssessmentRepository percipioAssessmentRepository) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiUrl2 = apiUrl2;
        this.organizationUuid = organizationUuid;
        this.jwtToken = jwtToken;
        this.traineesRepository = traineesRepository;
        this.traineeProgressRepository = traineeProgressRepository;
        this.percipioAssessmentRepository = percipioAssessmentRepository;
    }

    @Override
    public String generateRequestId() {
        String url = apiUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        String body = """
                {
                     "start": "2024-08-01T23:39:48Z",
                     "audience": "ALL",
                     "contentType": "Course,Linked Content,Scheduled Content,Assessment",
                     "csvPreferences": {
                         "header": true,
                         "rowDelimiter": "\\n",
                         "columnDelimiter": ",",
                         "headerForNoRecords": false
                     },
                     "sort": {
                         "field": "lastAccessDate",
                         "order": "asc"
                     },
                     "isFileRequiredInSftp": false,
                     "formatType": "JSON"
                 }
        """;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return extractRequestId(response.getBody());
    }

    @Override
    public String fetchData(String requestId) {
        String url = apiUrl2 + requestId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        parseJsonToDTO(response.getBody());
        return response.getBody();
    }

    @Override
    public List<UserContentAccessDTO> parseJsonToDTO(String jsonData) {
        List<UserContentAccessDTO> list = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);

            for (JsonNode node : rootNode) {
                int highScore = 0;
                try {
                    String highScoreValue = node.path("highScore").asText();
                    if (highScoreValue != null && !highScoreValue.isEmpty()) {
                        highScore = Integer.parseInt(highScoreValue);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid highScore value: " + e.getMessage());
                }

                // Add checks for integer fields with similar logic as above
                String durationStr = node.path("duration").asText();
                int duration = 0;
                if (durationStr != null && !durationStr.isEmpty()) {
                    try {
                        duration = Integer.parseInt(durationStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid duration value: " + e.getMessage());
                    }
                }

                String estimatedDurationStr = node.path("estimatedDuration").asText();
                int estimatedDuration = 0;
                if (estimatedDurationStr != null && !estimatedDurationStr.isEmpty()) {
                    try {
                        estimatedDuration = Integer.parseInt(estimatedDurationStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid estimatedDuration value: " + e.getMessage());
                    }
                }

                String totalAccessesStr = node.path("totalAccesses").asText();
                int totalAccesses = 0;
                if (totalAccessesStr != null && !totalAccessesStr.isEmpty()) {
                    try {
                        totalAccesses = Integer.parseInt(totalAccessesStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid totalAccesses value: " + e.getMessage());
                    }
                }

                UserContentAccessDTO dto = new UserContentAccessDTO(
                        null,
                        node.path("userId").asText(),
                        node.path("firstName").asText(),
                        node.path("lastName").asText(),
                        node.path("audience").asText(),
                        node.path("contentUuid").asText(),
                        node.path("contentTitle").asText(),
                        node.path("contentType").asText(),
                        node.path("languageCode").asText(),
                        node.path("category").asText(),
                        node.path("source").asText(),
                        node.path("status").asText(),
                        convertToTimestamp(node.path("completedDate").asText()),
                        duration,
                        estimatedDuration,
                        convertToTimestamp(node.path("firstAccess").asText()),
                        convertToTimestamp(node.path("lastAccess").asText()),
                        totalAccesses,
                        node.path("emailAddress").asText(),
                        node.path("durationHms").asText(),
                        node.path("estimatedDurationHms").asText(),
                        node.path("userUuid").asText(),
                        node.path("userStatus").asText(),
                        highScore
                );
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveUserContentAccessData(list);
        return list;
    }

    private void saveUserContentAccessData(List<UserContentAccessDTO> dtoList) {
        // Get the current time minus 5 minutes
        Instant fiveMinutesAgo = Instant.now().minus(100, ChronoUnit.DAYS);

        for (UserContentAccessDTO dto : dtoList) {
            // Check if LastAccess is within the last 5 minutes
            if (dto.getLastAccess() != null && dto.getLastAccess().toInstant().isAfter(fiveMinutesAgo)) {
                Trainees trainees = mapDTOToTrainees(dto);

                if (trainees != null) {
                    // Save TraineeProgress only if it does not exist
                    boolean traineeProgressExists = traineeProgressRepository.existsByTraineesAndCourseNameAndCompletionStatus(
                            trainees, dto.getContentTitle(), dto.getStatus());

                    if (!traineeProgressExists) {
                        TraineeProgress traineeProgress = mapDTOToTraineeProgress(dto, trainees);
                        if (traineeProgress != null) {
                            traineeProgressRepository.save(traineeProgress);
                        }
                    }

                    // Save PercipioAssessment only if it does not exist
                    boolean assessmentExists = percipioAssessmentRepository.existsByTraineesAndCourseName(
                            trainees, dto.getContentTitle());

                    if (!assessmentExists) {
                        PercipioAssessment percipioAssessment = mapDTOToPercipioAssessment(dto, trainees);
                        if (percipioAssessment != null) {
                            percipioAssessmentRepository.save(percipioAssessment);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processDataAndSaveToDatabase() {
        String requestId = generateRequestId();
        String jsonData = fetchData(requestId);
        List<UserContentAccessDTO> dtoList = parseJsonToDTO(jsonData);
        saveUserContentAccessData(dtoList);
    }

    private String extractRequestId(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("id").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Trainees mapDTOToTrainees(UserContentAccessDTO dto) {
        // Retrieve the trainee by percipio email and check if they are active
        Trainees existingTrainee = traineesRepository.findByPercipioEmailAndIsActive(dto.getUserId(), true);

        if (existingTrainee != null) {
            // Update the trainee's UUID if necessary
            existingTrainee.setUserUuid(UUID.fromString(dto.getUserUuid()));
        } else {
            // If no active trainee found, return null to avoid updating old records
            return null;
        }

        return existingTrainee;
    }

    private TraineeProgress mapDTOToTraineeProgress(UserContentAccessDTO dto, Trainees trainees) {
        TraineeProgress entity = new TraineeProgress();
        entity.setTrainees(trainees);
        entity.setDuration(dto.getDuration());
        entity.setEstimatedDuration(dto.getEstimatedDuration());
        entity.setCompletionStatus(dto.getStatus());
        entity.setCourseName(dto.getContentTitle());
        entity.setCompletedDate(dto.getLastAccess());

        return entity;
    }

    private PercipioAssessment mapDTOToPercipioAssessment(UserContentAccessDTO dto, Trainees trainees) {
        PercipioAssessment entity = new PercipioAssessment();
        entity.setTrainees(trainees);
        entity.setCourseName(dto.getContentTitle());
        entity.setScore(dto.getHighScore());

        return entity;
    }

    private Timestamp convertToTimestamp(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return null;
        }
        return Timestamp.from(Instant.parse(isoDate));
    }
}
