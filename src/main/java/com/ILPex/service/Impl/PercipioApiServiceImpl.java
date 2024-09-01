package com.ILPex.service.Impl;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.entity.PercipioAssessment;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.PercipioAssessmentRepository;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserContentAccessRepository;
import com.ILPex.service.PercipioApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
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
    private final UserContentAccessRepository userContentAccessRepository;
    private final TraineesRepository traineesRepository;
    private final TraineeProgressRepository traineeProgressRepository;
    private final PercipioAssessmentRepository percipioAssessmentRepository;

    public PercipioApiServiceImpl(RestTemplate restTemplate,
                                  @Value("${percipio.api.url}") String apiUrl,
                                  @Value("${percipio.api.url2}") String apiUrl2,
                                  @Value("${percipio.organization.uuid}") String organizationUuid,
                                  @Value("${percipio.api.jwt}") String jwtToken,
                                  UserContentAccessRepository userContentAccessRepository,
                                  TraineesRepository traineesRepository,
                                  TraineeProgressRepository traineeProgressRepository,
                                  PercipioAssessmentRepository percipioAssessmentRepository) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiUrl2 = apiUrl2;
        this.organizationUuid = organizationUuid;
        this.jwtToken = jwtToken;
        this.userContentAccessRepository = userContentAccessRepository;
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
                             "start": "2024-08-04T10:10:24Z",
                             "end": "2024-09-06T10:20:24Z",
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
                String highScoreValue = node.path("highScore").asText();
                int highScore = (highScoreValue != null && !highScoreValue.isEmpty())
                        ? Integer.parseInt(highScoreValue)
                        : 0;

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
                        Integer.parseInt(node.path("duration").asText()),
                        Integer.parseInt(node.path("estimatedDuration").asText()),
                        convertToTimestamp(node.path("firstAccess").asText()),
                        convertToTimestamp(node.path("lastAccess").asText()),
                        Integer.parseInt(node.path("totalAccesses").asText()),
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
        for (UserContentAccessDTO dto : dtoList) {
            Trainees trainees = mapDTOToTrainees(dto);

            if (trainees != null) {
                TraineeProgress traineeProgress = mapDTOToTraineeProgress(dto, trainees);
                if (traineeProgress != null) {
                    traineeProgressRepository.save(traineeProgress);
                }
            }

            PercipioAssessment percipioAssessment = mapDTOToPercipioAssessment(dto, trainees);
            if (percipioAssessment != null && percipioAssessment.getTrainees() != null) {
                percipioAssessmentRepository.save(percipioAssessment);
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
        Trainees existingTrainee = traineesRepository.findByPercipioEmail(dto.getUserId());

        if (existingTrainee != null) {
            existingTrainee.setUserUuid(UUID.fromString(dto.getUserUuid()));
        } else {
            return null;
        }

        return existingTrainee;
    }

    private TraineeProgress mapDTOToTraineeProgress(UserContentAccessDTO dto, Trainees trainees) {
        boolean exists = traineeProgressRepository.existsByTraineesAndCourseNameAndCompletionStatus(
                trainees, dto.getContentTitle(), "complete");

        if (exists) {
            return null;
        }

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
        Instant instant = Instant.parse(isoDate);
        return Timestamp.from(instant);
    }
}
