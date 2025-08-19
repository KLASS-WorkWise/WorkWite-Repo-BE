
package com.example.WorkWite_Repo_BE.controlers;

import java.util.Map;
import java.nio.file.Paths;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
// import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.services.UserService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController()
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserJpaRepository userJpaRepository;

    // @PreAuthorize("hasAnyRole('Administrators', 'Managers')")
    // @PreAuthorize("hasAnyRole('Administrators', 'Managers')")
    // API lấy danh sách user theo phân trang, trả về luôn ở endpoint /api/users
    @GetMapping()
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "1") int page) {
        int size = 10; // luôn lấy 10 user/trang
        var result = this.userService.getAllUsersPaginated(page, size);
        return ResponseEntity.ok(result);
    }

    // Lấy user theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            var user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    // @GetMapping("/paging")
    // public PaginatedUserResponseDto getAllUsersPaginated(
    // @RequestParam(defaultValue = "1") int page,
    // @RequestParam(defaultValue = "5") int size) {
    // System.out.println("page: " + page);
    // System.out.println("size: " + size);
    // return this.userService.getAllUsersPaginated(page, size);
    // }

    ;

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable("id") Long id,
            @RequestBody @Valid com.example.WorkWite_Repo_BE.dtos.UserDto.UserUpdateRequestDto request) {
        return this.userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
    }

    // @DeleteMapping("/soft-delete/{id}")
    // public void softDeleteUser(@PathVariable("id") Long id) {
    // this.userService.softDeleteUser(id);
    // }

    // @GetMapping("/get-all/deleted/false")
    // public List<UserResponseDto> findAvailableUsers() {
    // return this.userService.findAvailableUsers();
    // }

    // @GetMapping("/get-all/status")
    // public List<UserResponseDto> findByStatus(@RequestParam("status")
    // UserStatus status) {

    // return this.userService.findByStatus(status);
    // }

    // @GetMapping("/get-all/department/{id}")
    // public List<UserResponseDto> findByDepartment(@PathVariable("id") Long
    // departmentId) {

    // return this.userService.findByDepartmentId(departmentId);
    // }

    // @GetMapping("/get-all/name")
    // public List<UserProjection> findByName(@RequestParam("name") String name) {
    // return this.userService.findByNameContainingIgnoreCase(name);
    // }

    // @GetMapping("/get-all/email")
    // public List<UserProjection> findByEmail(@RequestParam("email") String email)
    // {
    // return this.userService.searchByEmailContainingIgnoreCase(email);
    // }

    @PatchMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("email") String email,
            @RequestParam("fullname") String fullname,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            User user = userJpaRepository.findByEmail(email)
                    .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));

            user.setFullName(fullname);

            // Nếu có file ảnh thì lưu file và cập nhật avatarUrl
            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);
                String avatarUrl = "/uploads/" + fileName;
                user.setAvatarUrl(avatarUrl);
            }

            userJpaRepository.save(user);

            return ResponseEntity.ok(java.util.Map.of("message", "Profile updated successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }
}