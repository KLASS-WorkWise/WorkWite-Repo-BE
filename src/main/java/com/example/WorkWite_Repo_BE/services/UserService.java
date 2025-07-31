
// ...existing code...
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateUserRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.PaginatedUserResponseDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UpdateUserRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserResponseDto;
import com.example.WorkWite_Repo_BE.entities.User;
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

    public UserService(UserJpaResponsitory userJpaResponsitory) {
        this.userJpaResponsitory = userJpaResponsitory;
    }

    public UserResponseDto createUser(CreateUserRequestDto userDto) {
        User user = new User();
        user.setUsername(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        User savedUser = userJpaResponsitory.save(user);
        return convertUserDto(savedUser);
    }

    private UserResponseDto convertUserDto(User user) {
        // Map User.username -> UserResponseDto.name, roles để empty list
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                java.util.Collections.emptyList());
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

    public UserResponseDto updateUser(String id, UpdateUserRequestDto userDto) {
        User existingUser = this.userJpaResponsitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + id));
        existingUser.setUsername(userDto.getUsername());
        existingUser.setPassword(userDto.getPassword());
        User updatedUser = this.userJpaResponsitory.save(existingUser);
        return convertUserDto(updatedUser);
    }

    // xóa user
    public void deleteUser(String id) {
        if (!userJpaResponsitory.existsById(id)) {
            throw new RuntimeException("Không tìm thấy user với id: " + id);
        }
        userJpaResponsitory.deleteById(id);
    }
}
