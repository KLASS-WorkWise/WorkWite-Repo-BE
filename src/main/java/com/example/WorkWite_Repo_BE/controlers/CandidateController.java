package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.dtos.CandidateDto.CandidatesResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.PaginatedCandidateResponseDto;
import com.example.WorkWite_Repo_BE.dtos.CandidateDto.UpdateCandidateRequestDto;
import com.example.WorkWite_Repo_BE.exceptions.IdInvalidException;
import com.example.WorkWite_Repo_BE.services.CandidatesServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/candidates")
public class CandidateController {
    private final CandidatesServices candidatesServices;

    public CandidateController(CandidatesServices candidatesServices) {
        this.candidatesServices = candidatesServices;
    }

    @GetMapping("")
    public ResponseEntity<PaginatedCandidateResponseDto> getAllCandidates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        System.out.println("page:" + page + " size:" + size);
        PaginatedCandidateResponseDto response = this.candidatesServices.getCandidatesPaginated(page - 1 , size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CandidatesResponseDto> updateCandidate(
            @PathVariable("id") Long id,
            @RequestBody UpdateCandidateRequestDto updateRequest) throws IdInvalidException {
        CandidatesResponseDto response = this.candidatesServices.updateCandidateById(id, updateRequest);
        if (response == null) {
//            return ResponseEntity.notFound().build();
            throw new IdInvalidException("Candidate not found with ID:" +id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //get candidate by id
    @GetMapping("/{id}")
    public ResponseEntity<CandidatesResponseDto> getCandidateById(@PathVariable("id") Long id) throws IdInvalidException {
        CandidatesResponseDto response = this.candidatesServices.getCandidateById(id);
        if (response == null) {
            throw new IdInvalidException("Candidate not found with ID:" +id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
