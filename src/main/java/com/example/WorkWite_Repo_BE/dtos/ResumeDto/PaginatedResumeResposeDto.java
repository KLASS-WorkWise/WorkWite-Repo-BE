package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PaginatedResumeResposeDto {
    private List<ResumeResponseDto> data;
    private int pageNumber;
    private int pageSize;
    private int totalRecords;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
