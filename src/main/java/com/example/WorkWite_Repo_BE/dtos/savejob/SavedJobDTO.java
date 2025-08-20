package com.example.WorkWite_Repo_BE.dtos.savejob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedJobDTO {
    private Long savedJobId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
}
