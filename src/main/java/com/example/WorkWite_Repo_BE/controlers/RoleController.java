
package com.example.WorkWite_Repo_BE.controlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateRoleRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UpdateRoleRequestDto;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.services.RoleService;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UserIdsRequestDto;

// ...existing code...

@RestController
@RequestMapping("/api/security/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping()
    public Role create(@RequestBody @Valid CreateRoleRequestDto data) {
        return roleService.create(data);
    }

    @PatchMapping("/{id}")
    public Role update(@PathVariable("id") String id, @RequestBody @Valid UpdateRoleRequestDto data) {
        return roleService.update(id, data);
    }

    @GetMapping()
    public Iterable<Role> getRoles() {
        return roleService.getRoles();
    }

    @PatchMapping("/{id}/add-users-to-role")
    public ResponseEntity<String> addUsersToRole(@PathVariable("id") String id,
            @RequestBody UserIdsRequestDto request) {
        roleService.addUsersToRole(id, request.getUserIds());
        return ResponseEntity.ok("Users added to role successfully!");
    }

    @PatchMapping("/{id}/remove-users-from-role")
    public ResponseEntity<String> removeUsersFromRole(@PathVariable("id") String id,
            @RequestBody UserIdsRequestDto request) {
        roleService.removeUsersFromRole(id, request.getUserIds());
        return ResponseEntity.ok("Users removed from role successfully!");
    }
}
