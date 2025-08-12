
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleUpdateRequestDto;
import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleResponseDto;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleService {

    private RoleResponseDto convertToDto(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }
        private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    
    
    // Thay đổi role của user
    public void changeUserRole(Long userId, Long newRoleId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
        Role newRole = roleJpaRepository.findById(newRoleId)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));
        user.getRoles().clear();
        user.getRoles().add(newRole);
        userJpaRepository.save(user);
    }





    // xóa role khỏi user
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
        Role role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userJpaRepository.save(user);
        }
    }

    // get tát cả role
    public List<RoleResponseDto> getAllRoles() {
        return roleJpaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
