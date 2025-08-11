package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleUpdateRequestDto;
import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleResponseDto;
import com.example.WorkWite_Repo_BE.services.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @PostMapping("/change")
    public void changeUserRole(@RequestParam Long userId, @RequestParam Long newRoleId) {
        roleService.changeUserRole(userId, newRoleId);
    }

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponseDto> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PatchMapping("/{id}")
    public RoleResponseDto updateRole(@PathVariable("id") Long id, @RequestBody RoleUpdateRequestDto request) {
        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
    }


    @PostMapping("/remove")
    public void removeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        roleService.removeRoleFromUser(userId, roleId);
    }
}
