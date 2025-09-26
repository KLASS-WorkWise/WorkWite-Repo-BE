package com.example.WorkWite_Repo_BE.dtos.ContactDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContactResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String message;
    private LocalDateTime createdAt;
}
