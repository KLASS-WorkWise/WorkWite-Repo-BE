package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateUserRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UpdateUserRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.entities.UserRole;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaResponsitory;
import com.example.WorkWite_Repo_BE.repositories.UserJpaResponsitory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserJpaResponsitory userJpaResponsitory;
    private final RoleJpaResponsitory roleJpaResponsitory;

    public UserService(UserJpaResponsitory userJpaResponsitory, RoleJpaResponsitory roleJpaResponsitory) {
        this.userJpaResponsitory = userJpaResponsitory;
        this.roleJpaResponsitory = roleJpaResponsitory;
    }

    public UserResponseDto createUser(CreateUserRequestDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPassword(userDto.getPassword());
        user.setEnabled(true);
        User savedUser = userJpaResponsitory.save(user);
        return convertUserDto(savedUser);
    }

    private UserResponseDto convertUserDto(User user) {
        List<String> roleNames = user.getUserRoles() == null ? List.of()
                : user.getUserRoles().stream()
                        .filter(ur -> ur.getRole() != null)
                        .map(ur -> ur.getRole().getName())
                        .collect(Collectors.toList());
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                roleNames);
    }

    public List<UserResponseDto> getAllUser() {
        List<User> user = this.userJpaResponsitory.findAll();
        return user.stream()
                .map(this::convertUserDto)
                .collect(Collectors.toList());
    }

    public PaginatedUserResponseDto getAllUserPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = this.userJpaResponsitory.findAll(pageable);
        List<UserResponseDto> userDtos = userPage.getContent().stream()
                .map(this::convertUserDto)
                .collect(Collectors.toList());
        return PaginatedUserResponseDto.builder()
                .data(userDtos)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalRecords(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    public UserResponseDto updateUser(Long id, UpdateUserRequestDto userDto) {
        User existingUser = this.userJpaResponsitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + id));
        existingUser.setUsername(userDto.getUsername());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setPassword(userDto.getPassword());
        User updatedUser = this.userJpaResponsitory.save(existingUser);
        return convertUserDto(updatedUser);
    }

    public void assignRoleToUser(Long userId, String roleName) {
        User user = userJpaResponsitory.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Role role = roleJpaResponsitory.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role: " + roleName));
        // Tạo UserRole mới
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setEnabled(true);
        if (user.getUserRoles() == null) {
            user.setUserRoles(new java.util.ArrayList<>());
        }
        user.getUserRoles().add(userRole);
        userJpaResponsitory.save(user);
    }
}
