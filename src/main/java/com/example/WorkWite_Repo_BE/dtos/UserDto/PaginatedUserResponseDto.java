package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;

@Data
@Builder
public class PaginatedUserResponseDto {
    private List<UserResponseDto> data;
    private int pageNumber;
    private int pageSize;
    private long totalRecords;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
