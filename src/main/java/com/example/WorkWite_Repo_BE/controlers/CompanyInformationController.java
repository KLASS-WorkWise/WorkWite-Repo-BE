package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CompanyInformationReponseDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.CreateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.PaginatedCompanyInformationRespondeDto;
import com.example.WorkWite_Repo_BE.dtos.CompanyInformation.UpdateCompanyInformationRequesDto;
import com.example.WorkWite_Repo_BE.services.CompanyInformationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/company")
public class CompanyInformationController {
    private  final CompanyInformationService companyInformationService;

    public CompanyInformationController(CompanyInformationService companyInformationService) {
        this.companyInformationService = companyInformationService;
    }


    @GetMapping("")
    public PaginatedCompanyInformationRespondeDto getAllCompanyInformation(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size
    ){
        return this.companyInformationService.getAllCompanies(page, size);
    }
    @PostMapping
    public CompanyInformationReponseDto saveCompanyInformation(@RequestBody CreateCompanyInformationRequesDto companyInformationRequesDto) {
        return companyInformationService.create(companyInformationRequesDto);
    }

    @GetMapping("/{id}")
    public CompanyInformationReponseDto getCompanyInformationById(@PathVariable Long id){
        return companyInformationService.getCompanyInformation(id);
    }
    @PatchMapping("/{id}")
    public CompanyInformationReponseDto updateCompanyInformationByid(@PathVariable Long id, UpdateCompanyInformationRequesDto updateCompanyInformationRequesDto) {
        return companyInformationService.updateCompany(id,updateCompanyInformationRequesDto);
    }
    @DeleteMapping("/{id}")
    public void deleteCompanyInformationById(@PathVariable Long id){
        companyInformationService.deleteCompany(id);
    }

}
