package com.example.WorkWite_Repo_BE.dtos.ContactDto;

import lombok.Data;

@Data
public class ContactRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String message;
}
