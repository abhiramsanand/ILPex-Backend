package com.ILPex.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PercipioApiService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiUrl2;
    private final String organizationUuid;
    private final String jwtToken;

    public PercipioApiService(RestTemplate restTemplate,
                              @Value("${percipio.api.url}") String apiUrl,
                              @Value("${percipio.api.url2}") String apiUrl2,
                              @Value("${percipio.organization.uuid}") String organizationUuid,
                              @Value("${percipio.api.jwt}") String jwtToken) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiUrl2 = apiUrl2;
        this.organizationUuid = organizationUuid;
        this.jwtToken = jwtToken;
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
        String url = apiUrl2;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

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
}
