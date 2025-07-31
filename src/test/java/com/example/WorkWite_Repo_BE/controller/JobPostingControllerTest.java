package com.example.WorkWite_Repo_BE.controller;

import com.example.WorkWite_Repo_BE.dtos.JobPostingRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.JobPostingResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobPostingControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateJobPosting() {
        JobPostingRequestDTO request = new JobPostingRequestDTO();
        request.setEmployerId(1L);
        request.setTitle("Software Engineer");
        request.setDescription("Develop web applications");
        request.setLocation("Remote");
        request.setSalaryRange("$80k-$120k");
        request.setJobType("FULL_TIME");
        request.setCategory("Engineering");
        request.setDeadline(LocalDate.parse("2025-12-31"));
        request.setStatus("OPEN");

        ResponseEntity<JobPostingResponseDTO> response = restTemplate.postForEntity("/api/job-postings", request, JobPostingResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Software Engineer", response.getBody().getTitle());
    }
}