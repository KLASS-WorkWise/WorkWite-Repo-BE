package com.example.WorkWite_Repo_BE.dtos.CompanyInformation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedCompanyInformationRespondeDto {
    private List<CompanyInformationReponseDto> data;
    private int pageNumber;
    private int pageSize;
    private Long totalRecords;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
