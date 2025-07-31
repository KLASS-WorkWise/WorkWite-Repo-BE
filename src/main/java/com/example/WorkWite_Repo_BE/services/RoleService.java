package com.example.WorkWite_Repo_BE.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.WorkWite_Repo_BE.dtos.UserDto.CreateRoleRequestDto;
import com.example.WorkWite_Repo_BE.dtos.UserDto.UpdateRoleRequestDto;
import com.example.WorkWite_Repo_BE.entities.Role;
import com.example.WorkWite_Repo_BE.entities.User;
import com.example.WorkWite_Repo_BE.entities.UserRole;
import com.example.WorkWite_Repo_BE.entities.UserRoleId;
import com.example.WorkWite_Repo_BE.repositories.RoleJpaResponsitory;
import com.example.WorkWite_Repo_BE.repositories.UserJpaResponsitory;
import com.example.WorkWite_Repo_BE.repositories.UserRoleJpaResponsitory;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoleService {
    private final RoleJpaResponsitory roleRepository;
    private final UserJpaResponsitory userRepository;
    private final UserRoleJpaResponsitory userRoleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RoleService(RoleJpaResponsitory roleRepository,
            UserJpaResponsitory userRepository,
            UserRoleJpaResponsitory userRoleRepository,
            ApplicationEventPublisher eventPublisher) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.eventPublisher = eventPublisher;
    }

    public Role create(CreateRoleRequestDto data) {
        Role role = new Role();
        role.setCode(data.getCode());
        role.setName(data.getName());
        role.setDescription(data.getDescription());
        return this.roleRepository.save(role);
    }

    public Role update(String id, UpdateRoleRequestDto role) {
        Role existingRole = this.roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: " + id));
        if (role.getCode() != null) {
            existingRole.setCode(role.getCode());
        }
        if (role.getName() != null) {
            existingRole.setName(role.getName());
        }
        if (role.getDescription() != null) {
            existingRole.setDescription(role.getDescription());
        }
        return this.roleRepository.save(existingRole);
    }

    public Role findById(String id) {
        return this.roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: " + id));
    }

    public List<Role> getRoles() {
        return this.roleRepository.findAll();
    }

    @Transactional
    public void addUsersToRole(String roleId, List<String> userIds) {
        List<User> users = userRepository.findByIdIn(userIds);
        Set<String> foundUserIds = users.stream().map(User::getId).collect(Collectors.toSet());
        for (String userId : userIds) {
            if (!foundUserIds.contains(userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found with id: " + userId);
            }
        }
        Role role = this.roleRepository.findById(roleId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: " + roleId));
        for (String userId : userIds) {
            UserRoleId userRoleId = new UserRoleId(userId, roleId);
            boolean exists = userRoleRepository.existsById(userRoleId);
            if (exists) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setId(userRoleId);
            User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null);
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
            // eventPublisher.publishEvent(new RoleAssignedEvent(user.getId(), roleId));
        }
    }

    public void removeUsersFromRole(String roleId, List<String> userIds) {
        Role role = this.roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found with id: " + roleId);
        }
        List<User> users = userRepository.findByIdIn(userIds);
        Set<String> foundUserIds = users.stream().map(User::getId).collect(Collectors.toSet());
        for (String userId : userIds) {
            if (!foundUserIds.contains(userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found with id: " + userId);
            }
        }
        for (String userId : userIds) {
            UserRoleId userRoleId = new UserRoleId(userId, roleId);
            userRoleRepository.deleteById(userRoleId);
            // eventPublisher.publishEvent(new RoleUnassignedEvent(userId, roleId));
        }
    }
}
