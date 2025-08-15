package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CreateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.PaginatedCompanyInformationRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.UpdateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.entities.CompanyInformation;
import com.example.WorkWite_Repo_BE.services.CompanyInformationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/company")
public class CompanyInformationController {
    private  final CompanyInformationService companyInformationService;

    public CompanyInformationController(CompanyInformationService companyInformationService) {
        this.companyInformationService = companyInformationService;
    }


    @GetMapping
    public PaginatedCompanyInformationRespondeDto getAllCompanyInformation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ){
        return this.companyInformationService.getAllCompanies(page, size);
    }
    // create
    @PostMapping("/{employerId}/company-info")
    public ResponseEntity<CompanyInformationReponseDto> createCompanyInfo(
            @PathVariable Long employerId,
            @Valid @RequestBody CreateCompanyInformationRequesDto dto) {

        CompanyInformationReponseDto savedCompanyInfo = companyInformationService.createCompanyInformation(employerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompanyInfo);
    }
    // update
    @PatchMapping("/{employerId}/update-company-info")
    public ResponseEntity<CompanyInformationReponseDto> patchCompanyInfo(
            @PathVariable Long employerId,
            @Valid @RequestBody UpdateCompanyInformationRequesDto dto) {

        CompanyInformationReponseDto updatedInfo =
                companyInformationService.patchCompanyInformation(employerId, dto);

        return ResponseEntity.ok(updatedInfo);
    }

    @GetMapping("/{id}")
    public CompanyInformationReponseDto getCompanyInformationById(@PathVariable Long id){
        return companyInformationService.getCompanyInformation(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCompanyInformationById(@PathVariable Long id){
        companyInformationService.deleteCompany(id);
    }

    @GetMapping("/search")
    public PaginatedCompanyInformationRespondeDto searchCompanies(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        return companyInformationService.searchCompaniesByName(name, page, size);
    }

}
