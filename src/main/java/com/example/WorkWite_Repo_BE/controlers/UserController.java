package com.example.WorkWite_Repo_BE.controlers;

// import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @PreAuthorize("hasAnyRole('Administrators', 'Managers')")
    // @PreAuthorize("hasAnyRole('Administrators', 'Managers')")
    @GetMapping()
    public List<UserResponseDto> getAllUsers() {
        return this.userService.getAllUsers();
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

    // @GetMapping("/{id}")
    // public UserResponseDto getUserById(@PathVariable("id") Long id) {
    // return this.userService.getUserById(id);
    // }

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
}