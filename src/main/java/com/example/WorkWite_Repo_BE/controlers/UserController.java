package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedStudentResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.repositories.UserProjection;
import com.example.WorkWite_Repo_BE.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/students")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @PreAuthorize("hasAnyRole('Administrators', 'Managers')")
    // @PreAuthorize("hasAnyRole('Administrators', 'Managers')")
    @GetMapping()
    public List<UserResponseDto> getAllUser() {
        return this.userService.getAllUser();
    }

    // @GetMapping("/paging")
    // public PaginatedStudentResponseDto getAllStudentsPaginated(
    // @RequestParam(defaultValue = "1") int page,
    // @RequestParam(defaultValue = "5") int size) {
    // System.out.println("page: " + page);
    // System.out.println("size: " + size);
    // return this.userService.getAllStudentsPaginated(page, size);
    // }

    // @PostMapping()
    // public UserResponseDto createStudent(@RequestBody @Valid
    // CreateStudentRequestDto createStudentRequestDto) {
    // return this.userService.createStudent(createStudentRequestDto);
    // }

    // @GetMapping("/{id}")
    // public UserResponseDto getStudentById(@PathVariable("id") Long id) {
    // return this.userService.getStudentById(id);
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
    // public void softDeleteStudent(@PathVariable("id") Long id) {
    // this.userService.softDeleteStudent(id);
    // }

    // @GetMapping("/get-all/deleted/false")
    // public List<UserResponseDto> findAvailableStudents() {
    // return this.stuuserServicedentService.findAvailableStudents();
    // }

    // @GetMapping("/get-all/status")
    // public List<UserResponseDto> findByStatus(@RequestParam("status")
    // StudentStatus status) {

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
