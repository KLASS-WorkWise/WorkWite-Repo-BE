package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.EmployersDto.CreateEmployerRequestDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.PaginatedEmployerRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.UpdateEmployerRequestDto;
import com.example.WorkWite_Repo_BE.services.EmployersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employers")
public class EmployersControler {
    private final EmployersService employersService;

    public EmployersControler(EmployersService employersService) {
        this.employersService = employersService;
    }

    @PatchMapping("/{id}/profile")
    public EmployerResponseDto updateEmployerProfile(@PathVariable Long id, @RequestBody UpdateEmployerRequestDto employerResponseDto){
        return employersService.updateEmployerProfile(id, employerResponseDto);
    }

}

