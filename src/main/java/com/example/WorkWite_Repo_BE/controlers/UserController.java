package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserUpdateRequestDto;
import com.example.WorkWite_Repo_BE.services.EmployersService;
import com.example.WorkWite_Repo_BE.services.UserService;
import com.example.WorkWite_Repo_BE.services.SystemLogService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmployersService employersService;
    private final SystemLogService systemLogService;

    // Lấy tất cả user (phân trang)
    @GetMapping()
    public PaginatedUserResponseDto getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return this.userService.getAllUsersPaginated(page, size);
    }

    // Lấy user theo id
    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return this.userService.getUserById(id);
    }

    // Update user
    @PatchMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        UserResponseDto response = this.userService.updateUser(id, userUpdateRequestDto);

        // Ghi log sửa user
        String actor = getCurrentUsernameOrEmail();
        String ipAddress = "unknown";
        systemLogService.saveLog(actor, "UPDATE_USER", "User updated", ipAddress, "INFO", id);

        return response;
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);

        // Ghi log xóa user
        String actor = getCurrentUsernameOrEmail();
        String ipAddress = "unknown";
        systemLogService.saveLog(actor, "DELETE_USER", "User deleted", ipAddress, "WARN", id);

        return ResponseEntity.ok("User with id " + id + " deleted successfully.");
    }

    // Lấy username hoặc email của người thực hiện thao tác
    private String getCurrentUsernameOrEmail() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication != null) {
            return authentication.getName();
        }
        return "unknown";
    }

    // Admin duyệt nâng cấp employer
    @PatchMapping("/approve-employer/{userId}")
    public ResponseEntity<?> approve(@PathVariable Long userId) {
        employersService.approveUpgrade(userId);
        return ResponseEntity.ok("Approved");
    }

    // Admin từ chối nâng cấp employer
    @PatchMapping("/reject-employer/{userId}")
    public ResponseEntity<?> reject(@PathVariable Long userId) {
        employersService.rejectUpgrade(userId);
        return ResponseEntity.ok("Rejected");
    }
}
