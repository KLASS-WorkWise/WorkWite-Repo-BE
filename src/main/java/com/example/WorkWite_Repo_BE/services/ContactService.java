package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.ContactDto.ContactRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.ContactDto.ContactResponseDTO;
import com.example.WorkWite_Repo_BE.entities.Contact;
import com.example.WorkWite_Repo_BE.repositories.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final EmailService emailService;

    public ContactResponseDTO createContact(ContactRequestDTO dto) {
        Contact contact = new Contact();
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setMessage(dto.getMessage());
        Contact saved = contactRepository.save(contact);

        // Gửi email cho admin
        String adminEmail = "vokhacdoai2003@gmail.com";
        String subject = "New Contact Message";
        String content = "<b>Name:</b> " + dto.getName() + "<br>"
                + "<b>Email:</b> " + dto.getEmail() + "<br>"
                + "<b>Phone:</b> " + dto.getPhone() + "<br>"
                + "<b>Message:</b> " + dto.getMessage();
        emailService.sendEmail(adminEmail, subject, content);

        ContactResponseDTO response = new ContactResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setPhone(saved.getPhone());
        response.setMessage(saved.getMessage());
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }
    public java.util.List<ContactResponseDTO> getAllContacts() {
        return contactRepository.findAll().stream().map(contact -> {
            ContactResponseDTO dto = new ContactResponseDTO();
            dto.setId(contact.getId());
            dto.setName(contact.getName());
            dto.setEmail(contact.getEmail());
            dto.setPhone(contact.getPhone());
            dto.setMessage(contact.getMessage());
            dto.setCreatedAt(contact.getCreatedAt());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
}
