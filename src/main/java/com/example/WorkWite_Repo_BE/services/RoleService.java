
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleUpdateRequestDto;
import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleResponseDto;
import com.example.WorkWite_Repo_BE.entities.Employers;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.exceptions.HttpException;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleService {

    // Thay đổi role của user (xóa hết role cũ, chỉ giữ role mới)
    @Transactional
    public void changeUserRole(Long userId, Long newRoleId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new HttpException("User not found", HttpStatus.NOT_FOUND));
        Role newRole = roleJpaRepository.findById(newRoleId)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));

        // Cập nhật role
        user.getRoles().clear();
        user.getRoles().add(newRole);
        userJpaRepository.save(user);

        String roleName = newRole.getName();

        if ("Employers".equalsIgnoreCase(roleName)) {
            // Thêm mới employer nếu chưa có
            boolean hasEmployer = employerRepository.existsByUserId(userId);
            if (!hasEmployer) {
                Employers employer = new Employers();
                employer.setUser(user); // set quan hệ tới User, tùy entity bạn chỉnh sửa
                // Có thể set các trường khác nếu cần
                employerRepository.save(employer);
            }
        } else if ("Users".equalsIgnoreCase(roleName)) {
            // Nếu chuyển sang role Users, xóa employer nếu có
            employerRepository.findByUserId(userId).ifPresent(employerRepository::delete);
        }
    }

    // Chuẩn hóa hàm convertToDto cho Role entity
    private RoleResponseDto convertToDto(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }


    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final EmployersJpaRepository employerRepository;

    public RoleResponseDto updateRole(Long id, RoleUpdateRequestDto request) {
        Role role = roleJpaRepository.findById(id)
                .orElseThrow(() -> new HttpException("Role not found", HttpStatus.NOT_FOUND));
        if (request.getName() != null)
            role.setName(request.getName());
        roleJpaRepository.save(role);
        return convertToDto(role);
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
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
