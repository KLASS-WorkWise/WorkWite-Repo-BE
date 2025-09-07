package com.example.WorkWite_Repo_BE.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper for consistency across all controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message; // Can be String or List<String>
    private T data;
}
