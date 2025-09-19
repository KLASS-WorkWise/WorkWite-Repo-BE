package com.example.WorkWite_Repo_BE.dtos.SystemLogDto;

import lombok.Data;

@Data
public class SystemLogRequestDTO {
    private String actor;
    private String action;
    private String description;
}
