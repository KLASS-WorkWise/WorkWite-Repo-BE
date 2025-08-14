package com.example.WorkWite_Repo_BE.exceptions;

import com.example.WorkWite_Repo_BE.api.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationExceptions (MethodArgumentNotValidException ex){
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fielName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fielName, k -> new ArrayList<>()).add(errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleGeneraExceptions (Exception ex){
        Map<String, List<String>> errors = new HashMap<>();
        errors.computeIfAbsent("Error", k -> new ArrayList<>()).add(ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {IdInvalidException.class})
    public ResponseEntity<RestResponse<Object>> hanldException(IdInvalidException idInvalidException) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(idInvalidException.getMessage());
        res.setMessage("Id invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }


}

