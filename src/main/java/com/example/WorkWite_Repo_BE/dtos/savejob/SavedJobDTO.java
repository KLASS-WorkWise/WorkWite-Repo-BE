package com.example.movie.dtos.savejob;

import lombok.*;

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
