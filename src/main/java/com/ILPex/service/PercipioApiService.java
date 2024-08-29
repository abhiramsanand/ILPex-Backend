package com.ILPex.service;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.entity.PercipioAssessment;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import com.ILPex.entity.UserContentAccess;
import com.ILPex.repository.PercipioAssessmentRepository;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserContentAccessRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PercipioApiService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiUrl2;
    private final String organizationUuid;
    private final String jwtToken;
    private final UserContentAccessRepository userContentAccessRepository;
    private final TraineesRepository traineesRepository;
    private final TraineeProgressRepository traineeProgressRepository;
    private final PercipioAssessmentRepository percipioAssessmentRepository;

    public PercipioApiService(RestTemplate restTemplate,
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

    public String generateRequestId() {
        String url = apiUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        String body = """
                {
                             "start": "2024-08-25T10:10:24Z",
                             "end": "2024-08-28T10:20:24Z",
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
                                 "order": "desc"
                             },
                             "isFileRequiredInSftp": false,
                             "formatType": "JSON"
                         }
        """;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // Extract request ID from the response
        String requestId = extractRequestId(response.getBody());
        return requestId;
    }

    public String fetchData(String requestId) {
        String url = apiUrl2 + requestId; // Assuming requestId is used in the URL to fetch data

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        parseJsonToDTO(response.getBody());
        return response.getBody();
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

    private List<UserContentAccessDTO> parseJsonToDTO(String jsonData) {
        List<UserContentAccessDTO> list = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);

            // Iterate over the JSON array
            for (JsonNode node : rootNode) {
                // Extract highScore as a String
                String highScoreValue = node.path("highScore").asText();

                // Default highScore to 0 or handle it appropriately if it's empty
                int highScore = (highScoreValue != null && !highScoreValue.isEmpty())
                        ? Integer.parseInt(highScoreValue)
                        : 0; // Default value can be set to 0 or another appropriate value

                // Create the DTO with the parsed or default highScore
                UserContentAccessDTO dto = new UserContentAccessDTO(
                        null, // id will be auto-generated
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
                        highScore // Use the parsed or default highScore
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
            // Map DTO to UserContentAccess entity
//            UserContentAccess userContentAccess = mapDTOToUserContentAccess(dto);
//            userContentAccessRepository.save(userContentAccess);

            // Handle Trainees separately
            Trainees trainees = mapDTOToTrainees(dto);

            // Map DTO to TraineeProgress entity separately and save
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

    private UserContentAccess mapDTOToUserContentAccess(UserContentAccessDTO dto) {
        UserContentAccess entity = new UserContentAccess();
        entity.setUserId(dto.getUserId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAudience(dto.getAudience());
        entity.setContentUuid(UUID.fromString(dto.getContentUuid()));
        entity.setContentTitle(dto.getContentTitle());
        entity.setContentType(dto.getContentType());
        entity.setLanguageCode(dto.getLanguageCode());
        entity.setCategory(dto.getCategory());
        entity.setSource(dto.getSource());
        entity.setStatus(dto.getStatus());
        entity.setCompletedDate(dto.getCompletedDate());
        entity.setDuration(dto.getDuration());
        entity.setEstimatedDuration(dto.getEstimatedDuration());
        entity.setFirstAccess(dto.getFirstAccess());
        entity.setLastAccess(dto.getLastAccess());
        entity.setTotalAccesses(dto.getTotalAccesses());
        entity.setEmailAddress(dto.getEmailAddress());
        entity.setDurationHms(dto.getDurationHms());
        entity.setEstimatedDurationHms(dto.getEstimatedDurationHms());
        entity.setUserUuid(UUID.fromString(dto.getUserUuid()));
        entity.setUserStatus(dto.getUserStatus());

        return entity;
    }

    private Trainees mapDTOToTrainees(UserContentAccessDTO dto) {
        // Find the existing Trainees entity by percipio_email (which corresponds to userId)
        Trainees existingTrainee = traineesRepository.findByPercipioEmail(dto.getUserId());

        if (existingTrainee != null) {
            // Populate the userUuid based on the DTO
            existingTrainee.setUserUuid(UUID.fromString(dto.getUserUuid()));
        } else {
            // If no match is found, you can either handle this scenario (e.g., throw an exception)
            // or simply return null, depending on your needs.
            // For now, let's return null:
            return null;
        }

        return existingTrainee;
    }


    private TraineeProgress mapDTOToTraineeProgress(UserContentAccessDTO dto, Trainees trainees) {

        // Check if the completion status for this course is already complete for the given trainee
        boolean exists = traineeProgressRepository.existsByTraineesAndCourseNameAndCompletionStatus(
                trainees,
                dto.getContentTitle(),
                "complete"
        );

        // If a record with "complete" status already exists, do not map and save again
        if (exists) {
            return null; // or throw an exception if you prefer
        }

            TraineeProgress entity = new TraineeProgress();
            entity.setTrainees(trainees); // Associate the Trainees entity
            entity.setDuration(dto.getDuration()); // Map duration to duration column
            entity.setEstimatedDuration(dto.getEstimatedDuration()); // Map estimated_duration to estimated_duration column
            entity.setCompletionStatus(dto.getStatus()); // Map status to completion_status column
            entity.setCourseName(dto.getContentTitle()); // Map contentTitle to course_name column
            entity.setCompletedDate(dto.getLastAccess()); // Map completedDate to the entity
            // Other mappings as needed
            return entity;
        }

    private PercipioAssessment mapDTOToPercipioAssessment(UserContentAccessDTO dto, Trainees trainees) {
        PercipioAssessment entity = new PercipioAssessment();

        entity.setTrainees(trainees);
        entity.setCourseName(dto.getContentTitle());
        entity.setScore(dto.getHighScore());

        return entity;
    }

    public void processDataAndSaveToDatabase() {
        String requestId = generateRequestId();
        String jsonData = fetchData(requestId);
        List<UserContentAccessDTO> dtoList = parseJsonToDTO(jsonData);
        saveUserContentAccessData(dtoList);
    }

    private Timestamp convertToTimestamp(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return null;
        }
        Instant instant = Instant.parse(isoDate);
        return Timestamp.from(instant);
    }
}
