package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.ContactDto.ContactRequestDTO;
import com.example.WorkWite_Repo_BE.dtos.ContactDto.ContactResponseDTO;
import com.example.WorkWite_Repo_BE.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Object> createContact(@RequestBody ContactRequestDTO dto) {
        try {
            // Basic validation
            if (dto.getEmail() == null || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") ) {
                return ResponseEntity.badRequest().body(
                    java.util.Map.of("success", false, "message", "Invalid email format.")
                );
            }
            if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    java.util.Map.of("success", false, "message", "Message cannot be empty.")
                );
            }
            if (dto.getPhone() != null && !dto.getPhone().matches("^[0-9+\\- ]{8,15}$")) {
                return ResponseEntity.badRequest().body(
                    java.util.Map.of("success", false, "message", "Invalid phone format.")
                );
            }
            ContactResponseDTO response = contactService.createContact(dto);
            return ResponseEntity.ok(
                java.util.Map.of(
                    "success", true,
                    "message", "Your contact request has been submitted successfully.",
                    "data", response
                )
            );
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.status(500).body(
                java.util.Map.of("success", false, "message", "Failed to save contact information. Please try again later.")
            );
        } catch (Exception e) {
            // Nếu lỗi liên quan đến gửi email, trả về thông báo lỗi email
            if (e.getCause() != null && e.getCause().getClass().getName().contains("MessagingException")) {
                return ResponseEntity.status(500).body(
                    java.util.Map.of("success", false, "message", "Unable to send email notification.")
                );
            }
            return ResponseEntity.status(500).body(
                java.util.Map.of("success", false, "message", "Internal server error. Please contact support.")
            );
        }
    }
        @GetMapping
        public ResponseEntity<java.util.List<ContactResponseDTO>> getAllContacts() {
            java.util.List<ContactResponseDTO> contacts = contactService.getAllContacts();
            return ResponseEntity.ok(contacts);
        }
}
