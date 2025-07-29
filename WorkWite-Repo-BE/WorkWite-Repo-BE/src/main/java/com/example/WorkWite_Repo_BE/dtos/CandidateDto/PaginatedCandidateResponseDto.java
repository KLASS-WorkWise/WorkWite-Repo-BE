package com.example.WorkWite_Repo_BE.dtos.CandidateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PaginatedCandidateResponseDto {
    private List<CandidatesResponseDto> data;
    private int pageNumber;
    private int pageSize;
    private int totalRecords;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
