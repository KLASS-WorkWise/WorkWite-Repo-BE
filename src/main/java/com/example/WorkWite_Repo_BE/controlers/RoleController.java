package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateRoleRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.RoleResponseDto;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaResponsitory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/role")
public class RoleController {
    private final RoleJpaResponsitory roleJpaResponsitory;

    public RoleController(RoleJpaResponsitory roleJpaResponsitory) {
        this.roleJpaResponsitory = roleJpaResponsitory;
    }

    // Tạo mới role (sử dụng DTO)
    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@RequestBody CreateRoleRequestDto requestDto) {
        Role role = new Role();
        role.setName(requestDto.getName());
        role.setCode(requestDto.getCode());
        role.setDescription(requestDto.getDescription());
        Role savedRole = roleJpaResponsitory.save(role);
        RoleResponseDto responseDto = new RoleResponseDto(
                savedRole.getId() != null ? savedRole.getId().toString() : null,
                savedRole.getCode(),
                savedRole.getName());
        return ResponseEntity.ok(responseDto);
    }

    // Lấy danh sách tất cả role (sử dụng DTO)
    @GetMapping
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleJpaResponsitory.findAll();
        return roles.stream()
                .map(role -> new RoleResponseDto(
                        role.getId() != null ? role.getId().toString() : null,
                        role.getCode(),
                        role.getName()))
                .toList();
    }
}
