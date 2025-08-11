
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
    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public RoleResponseDto updateRole(Long id, RoleUpdateRequestDto request) {
        Role role = roleJpaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));
        if (request.getName() != null)
            role.setName(request.getName());
        roleJpaRepository.save(role);
        return new RoleResponseDto(role.getId(), role.getName());
    }

    public void deleteRole(Long id) {
        if (!roleJpaRepository.existsById(id)) {
            throw new HttpException("Role not found", HttpStatus.NOT_FOUND);
        }
        // Gỡ role khỏi tất cả user trước khi xóa role
        Role role = roleJpaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));
        List<User> usersWithRole = userJpaRepository.findAll().stream()
                .filter(u -> u.getRoles() != null && u.getRoles().contains(role))
                .collect(Collectors.toList());
        for (User user : usersWithRole) {
            user.getRoles().remove(role);
            userJpaRepository.save(user);
        }
        roleJpaRepository.deleteById(id);
    }

    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
        Role role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userJpaRepository.save(user);
        }
    }

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

    public List<RoleResponseDto> getAllRoles() {
        return roleJpaRepository.findAll().stream()
                .map(r -> new RoleResponseDto(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }
}
