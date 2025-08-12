package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleUpdateRequestDto;
import com.example.WorkWite_Repo_BE.dtos.RoleDto.RoleResponseDto;
import com.example.WorkWite_Repo_BE.services.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleResponseDto> getAllRoles() {
        return roleService.getAllRoles();
    }

    // thay đổi role của user
    @PostMapping("/change/{userId}/{newRoleId}")
    public void changeUserRole(@PathVariable Long userId, @PathVariable Long newRoleId) {
        roleService.changeUserRole(userId, newRoleId);
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
