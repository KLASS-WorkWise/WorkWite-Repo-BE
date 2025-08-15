package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedEmployerRespondeDto {
    private List<EmployerResponseDto> data;
    private int pageNumber;
    private int pageSize;
    private Long totalRecords;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}