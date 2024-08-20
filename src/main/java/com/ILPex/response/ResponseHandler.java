package com.ILPex.response;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHandler {

    // Static inner class for error responses
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static ResponseEntity<Object> responseBuilder(String message, HttpStatus status, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("data", data);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON) // Ensure this line is present
                .body(body);
    }

    public static ResponseEntity<ErrorResponse> errorBuilder(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON) // Ensure this line is present
                .body(errorResponse);
    }
}

