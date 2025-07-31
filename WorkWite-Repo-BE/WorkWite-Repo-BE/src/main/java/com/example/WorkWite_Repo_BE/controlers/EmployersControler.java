package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.EmployersDto.EmployerResponseDto;
import com.example.WorkWite_Repo_BE.dtos.EmployersDto.PaginatedEmployerRespondeDto;
import com.example.WorkWite_Repo_BE.services.EmployersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employers")
public class EmployersControler {
    private final EmployersService employersService;

    public EmployersControler(EmployersService employersService) {
        this.employersService = employersService;
    }

    @GetMapping("")
    public PaginatedEmployerRespondeDto getAllEmployers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size
    ){
        return this.employersService.getAllEmployers(page, size);
    }

    @PostMapping
    public EmployerResponseDto save(@RequestBody EmployerResponseDto employerResponseDto){
        return employersService.createEmployer(employerResponseDto);
    }
    @GetMapping("/{id}")
    public EmployerResponseDto getEmployerById(@PathVariable Long id){
        return employersService.getEmployerById(id);
    }
    @PatchMapping("/{id}")
    public EmployerResponseDto updateEmployerById(@PathVariable Long id, @RequestBody EmployerResponseDto employerResponseDto){
        return employersService.updateEmployerById(id, employerResponseDto);
    }
    @DeleteMapping("/{id}")
    public void deleteEmployerById(@PathVariable Long id){
        employersService.deleteEmployerById(id);
    }
}

