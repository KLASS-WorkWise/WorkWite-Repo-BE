package com.example.WorkWite_Repo_BE.dtos.UserDto;

import lombok.Data;
import java.util.List;

@Data
public class UserIdsRequestDto {
    private List<String> userIds;
}
