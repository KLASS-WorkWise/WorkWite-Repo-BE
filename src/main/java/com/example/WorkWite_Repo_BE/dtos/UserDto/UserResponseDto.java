package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //Thêm field mới
    //Xóa field không quan trọng , ko sinh ra ID mới khi tác động

    private String id;
    private String name;
    private String email;
    private List<RoleResponseDto> roles;
}
