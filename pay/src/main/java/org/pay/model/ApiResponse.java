package org.pay.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private int statusCode;

    @JsonInclude(Include.NON_NULL)
    private T data;

    public ApiResponse(String message, int statusCode, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return new ApiResponse<>(message, statusCode, data);
    }

    public static <T> ApiResponse<T> failure(String message, int statusCode) {
        return new ApiResponse<>(message, statusCode, null);
    }
}
