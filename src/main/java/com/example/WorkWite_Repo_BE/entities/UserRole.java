package com.example.WorkWite_Repo_BE.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
public class UserRole {
    @EmbeddedId
    private UserRoleId id = new UserRoleId();   // UserRoleId là class chứa 2 field: userId và roleId → nó sẽ làm khóa chính kép (PK) cho bảng user_roles

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")  //field user này gắn với khóa chính userId trong UserRoleId.
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roleId")
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;
}
