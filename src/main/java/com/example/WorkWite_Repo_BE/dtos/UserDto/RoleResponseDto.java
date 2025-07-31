package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

// (dùng để trả về thông tin role ra ngoài API)
/**
 * DTO for representing role information in API responses.
 * Contains role basic information.
 */
@Data
@Builder
@NoArgsConstructor
public class RoleResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;



    // public dưới laf để format theo cấu trúc dễ đọc hiểu nhất dạng    :

//    RoleResponseDto dto = RoleResponseDto.builder()
//            .id("1")
//            .code("ADMIN")
//            .name("Administrator")


    public RoleResponseDto(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}