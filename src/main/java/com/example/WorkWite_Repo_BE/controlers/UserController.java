package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateUserRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UpdateUserRequestDto;
import com.example.WorkWite_Repo_BE.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    // String message = "hello bạn đầu với project Crud";

    // khởi tạo cóntructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody CreateUserRequestDto userDto) {
        return userService.createUser(userDto);
    }

    // lấy toàn bộ user theo phân trang :
    @GetMapping("/allusser")
    public PaginatedUserResponseDto getHetAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        System.out.println("page: " + page);
        System.out.println("size: " + size);
        return this.userService.getAllUserPaginated(page, size); // gọi đúng tên hàm
    }

    // update user theo id
    @PatchMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDto userDto) {
        return userService.updateUser(id, userDto);
    }

    // phân quyền cho user
    @PostMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<?> assignRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        userService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok("Đã gán role " + roleName + " cho user " + userId);
    }

}