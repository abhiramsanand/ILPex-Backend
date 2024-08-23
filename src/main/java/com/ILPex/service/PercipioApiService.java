package com.ILPex.service;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.entity.UserContentAccess;
import com.ILPex.repository.UserContentAccessRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import java.sql.Timestamp;
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

    public PercipioApiService(RestTemplate restTemplate,
                              @Value("${percipio.api.url}") String apiUrl,
                              @Value("${percipio.api.url2}") String apiUrl2,
                              @Value("${percipio.organization.uuid}") String organizationUuid,
                              @Value("${percipio.api.jwt}") String jwtToken,
                              UserContentAccessRepository userContentAccessRepository) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiUrl2 = apiUrl2;
        this.organizationUuid = organizationUuid;
        this.jwtToken = jwtToken;
        this.userContentAccessRepository = userContentAccessRepository;
    }

    public String generateRequestId() {
        String url = apiUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        String body = """
        {
            "timeFrame": "DAY",
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
        String url = apiUrl2; // Assuming requestId is used in the URL to fetch data

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        parseJsonToDTO(response.toString());
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
            // Check if the response is not JSON
            jsonData = jsonData.substring(jsonData.indexOf(",") + 1, jsonData.lastIndexOf(">"));
            if (jsonData.trim().startsWith("<200 ")) {
                System.err.println("Received HTML response instead of JSON: " + jsonData);
                return list; // Return empty list or handle the error as needed
            }
            System.err.println("Received HTML response instead of JSON: " + jsonData);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);

            // Iterate over the JSON array
            for (JsonNode node : rootNode) {

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
                        node.path("userStatus").asText()
                );
                System.out.println("User Name"+dto.getFirstName());
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Length"+list.size());
        saveUserContentAccessData(list);
        return list;
    }



    private UserContentAccess mapDTOToEntity(UserContentAccessDTO dto) {
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

    public void saveUserContentAccessData(List<UserContentAccessDTO> dtoList) {
        System.out.println("Length"+dtoList.size());
        for (UserContentAccessDTO dto : dtoList) {
            UserContentAccess entity = mapDTOToEntity(dto);
            System.out.println("UserId"+entity.getFirstName());
            userContentAccessRepository.save(entity);
        }
    }

    public void processDataAndSaveToDatabase() {
        String requestId = generateRequestId();
        String jsonData = fetchData(requestId);
        List<UserContentAccessDTO> dtoList = parseJsonToDTO(jsonData);
        System.out.println("Length"+dtoList.size());
        saveUserContentAccessData(dtoList);
    }

    private Timestamp convertToTimestamp(String isoDate) {
        System.out.println("Format" + isoDate);
        if (isoDate == null || isoDate.trim().isEmpty()) {
            System.err.println("Invalid date string: " + isoDate);
            return null; // or return a default value if needed
        }
        // Parse the ISO 8601 date string to Instant
        Instant instant = Instant.parse(isoDate);
        // Convert Instant to Timestamp
        System.out.println("Formatted time" + Timestamp.from(instant));
        return Timestamp.from(instant);
    }
}
