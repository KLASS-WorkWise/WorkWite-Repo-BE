package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CreatCandidateRequest;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.PaginatedCandidateResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.UpdateCandidateRequestDto;
import com.example.WorkWite_Repo_BE.services.CandidatesServices;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/candidates")

public class CandidateController {
    private final CandidatesServices candidatesServices;

    public CandidateController(CandidatesServices candidatesServices) {
        this.candidatesServices = candidatesServices;
    }

    @GetMapping("")
    public PaginatedCandidateResponseDto getAllCandidates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        System.out.println("page:" + page + " size:" + size);
        return this.candidatesServices.getCandidatesPaginated(page - 1 , size);
    }

    @PostMapping
    public CandidatesResponseDto createCandidate(@RequestBody CreatCandidateRequest creatCandidate) {
        return this.candidatesServices.creatCandidate(creatCandidate);
    }

    @PatchMapping("/{id}")
    public CandidatesResponseDto updateCandidate(
            @PathVariable("id") Long id,
            @RequestBody UpdateCandidateRequestDto updateRequest) {
        return this.candidatesServices.updateCandidateById(id, updateRequest);
    }

    // DELETE by ID
    @DeleteMapping("/{id}")
    public void deleteCandidate(@PathVariable("id") Long id) {
        this.candidatesServices.deletaCandidateById(id);
    }
}
