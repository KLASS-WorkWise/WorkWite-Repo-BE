package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.*;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.ApplicantHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantHistoryService {

    private final ApplicantHistoryRepository applicantHistoryRepository;

    public List<ApplicantHistoryDto> getHistory(Long applicantId) {
        return applicantHistoryRepository.findByApplicantIdOrderByChangedAtAsc(applicantId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<ApplicantTimelineDto> getFullTimeline(Applicant applicant) {
        List<ApplicantHistoryDto> history = getHistory(applicant.getId());

        Map<ApplicationStatus, List<ApplicantHistoryDto>> grouped =
                history.stream().collect(Collectors.groupingBy(
                        ApplicantHistoryDto::getStatus,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Thứ tự chuẩn
        List<ApplicationStatus> orderedSteps = List.of(
                ApplicationStatus.PENDING,
                ApplicationStatus.INTERVIEW,
                ApplicationStatus.OFFER,
                ApplicationStatus.HIRED,
                ApplicationStatus.REJECTED
        );

        ApplicationStatus currentStatus = applicant.getApplicationStatus();
        int currentIndex = orderedSteps.indexOf(currentStatus);

        List<ApplicantTimelineDto> timeline = new ArrayList<>();
        for (int i = 0; i < orderedSteps.size(); i++) {
            ApplicationStatus step = orderedSteps.get(i);
            timeline.add(
                    ApplicantTimelineDto.builder()
                            .stepOrder(i + 1)
                            .status(step)
                            .events(grouped.getOrDefault(step, new ArrayList<>()))
                            .currentStep(i == currentIndex)
                            .completed(i < currentIndex)
                            .build()
            );
        }
        return timeline;
    }

    private ApplicantHistoryDto convertToDto(ApplicantHistory history) {
        return ApplicantHistoryDto.builder()
                .id(history.getId())
                .status(history.getStatus())
                .note(history.getNote())
                .changedAt(history.getChangedAt())
                .changedBy(history.getChangedBy())
                .build();
    }
}
