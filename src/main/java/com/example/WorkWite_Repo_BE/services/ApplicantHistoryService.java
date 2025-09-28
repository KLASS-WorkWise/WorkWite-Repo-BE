package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.dtos.applicant.*;
import com.example.WorkWite_Repo_BE.entities.*;
import com.example.WorkWite_Repo_BE.enums.ApplicationStatus;
import com.example.WorkWite_Repo_BE.repositories.ApplicantHistoryRepository;
import com.example.WorkWite_Repo_BE.repositories.InterviewScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantHistoryService {

    private final ApplicantHistoryRepository applicantHistoryRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;

    public List<ApplicantHistoryDto> getHistory(Long applicantId) {
        return applicantHistoryRepository.findByApplicantIdOrderByChangedAtAsc(applicantId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<TimelineEventResponse> getFullTimeline(Applicant applicant) {
        List<ApplicantHistoryDto> history = getHistory(applicant.getId());

        // Gom history theo status
        Map<ApplicationStatus, List<ApplicantHistoryDto>> grouped =
                history.stream().collect(Collectors.groupingBy(
                        ApplicantHistoryDto::getStatus,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Thứ tự chuẩn các bước
        List<ApplicationStatus> orderedSteps = List.of(
                ApplicationStatus.PENDING,
                ApplicationStatus.CV_REVIEW,
                ApplicationStatus.INTERVIEW,
                ApplicationStatus.OFFER,
                ApplicationStatus.HIRED,
                ApplicationStatus.REJECTED
        );

        ApplicationStatus currentStatus = applicant.getApplicationStatus();
        int currentIndex = orderedSteps.indexOf(currentStatus);

        List<TimelineEventResponse> timeline = new ArrayList<>();

        for (int i = 0; i < orderedSteps.size(); i++) {
            ApplicationStatus step = orderedSteps.get(i);

            boolean hasHistory = grouped.containsKey(step);
            boolean isCurrentStep = step == currentStatus;

            // Nếu bước này không có history và không phải current → bỏ qua
            if (!hasHistory && !isCurrentStep) {
                continue;
            }

            List<Object> events = new ArrayList<>();
            events.addAll(grouped.getOrDefault(step, new ArrayList<>()));

            // Thêm lịch phỏng vấn nếu là bước INTERVIEW
            if (step == ApplicationStatus.INTERVIEW) {
                List<InterviewScheduleDto> schedules =
                        interviewScheduleRepository.findAll().stream()
                                .filter(s -> s.getApplicant().getId().equals(applicant.getId()))
                                .map(s -> InterviewScheduleDto.builder()
                                        .id(s.getId())
                                        .scheduledAt(s.getScheduledAt())
                                        .location(s.getLocation())
                                        .interviewer(s.getInterviewer())
                                        .build())
                                .toList();
                events.addAll(schedules);
            }

            boolean completed = i < currentIndex;

            timeline.add(
                    TimelineEventResponse.builder()
                            .stepOrder(timeline.size() + 1) // số thứ tự liền mạch
                            .status(step)
                            .events(events)
                            .currentStep(isCurrentStep)
                            .completed(completed)
                            .build()
            );

            // Nếu là REJECTED hoặc HIRED → dừng vòng lặp
            if (isCurrentStep && (currentStatus == ApplicationStatus.REJECTED || currentStatus == ApplicationStatus.HIRED)) {
                break;
            }
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
