package org.pay.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApiResponse {
    private HttpStatus status;
    private String message;
    private Map<String, Object> data;
    private LocalDateTime timestamp;

    public ApiResponse(HttpStatus status, String message, Map<String, Object> data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
