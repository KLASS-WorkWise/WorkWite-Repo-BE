
package com.example.WorkWite_Repo_BE.controlers;

// import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserUpdateRequestDto;
import com.example.WorkWite_Repo_BE.services.EmployersService;
import com.example.WorkWite_Repo_BE.services.UserService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final EmployersService  employersService;

    public UserController(UserService userService, EmployersService employersService) {
        this.userService = userService;
        this.employersService = employersService;
    }

    //    gett all user
    @GetMapping()
    public PaginatedUserResponseDto getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
//     System.out.println("page: " + page);
//     System.out.println("size: " + size);
        return this.userService.getAllUsersPaginated(page, size);
    }

    // Lấy user theo id
    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return this.userService.getUserById(id);
    }




//    update usser
    @PatchMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable("id") Long id,
            @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto ) {
        return this.userService.updateUser(id, userUpdateRequestDto);
    }

//    delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok("User with id " + id + " deleted successfully.");
    }

    // admin duyệt
    @PatchMapping("/approve-employer/{userId}")
    public ResponseEntity<?> approve(@PathVariable Long userId){
        employersService.approveUpgrade(userId);
        return ResponseEntity.ok("Approved");
    }

    // admin từ chối
    @PatchMapping("/reject-employer/{userId}")
    public ResponseEntity<?> reject(@PathVariable Long userId){
        employersService.rejectUpgrade(userId);
        return ResponseEntity.ok("Rejected");
    }
}