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

        Map<ApplicationStatus, List<ApplicantHistoryDto>> grouped =
                history.stream().collect(Collectors.groupingBy(
                        ApplicantHistoryDto::getStatus,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Thứ tự chuẩn
        List<ApplicationStatus> orderedSteps = List.of(
//                ApplicationStatus.APPLIED,     // ứng viên đã nộp
                ApplicationStatus.CV_REVIEW,   // xét CV
                ApplicationStatus.INTERVIEW,   // phỏng vấn
                ApplicationStatus.OFFER,       // gửi offer
                ApplicationStatus.HIRED,       // nhận vào làm
                ApplicationStatus.REJECTED     // loại
        );

        ApplicationStatus currentStatus = applicant.getApplicationStatus();
        int currentIndex = orderedSteps.indexOf(currentStatus);

        List<TimelineEventResponse> timeline = new ArrayList<>();
        for (int i = 0; i < orderedSteps.size(); i++) {
            ApplicationStatus step = orderedSteps.get(i);

            List<Object> events = new ArrayList<>();
            // Lấy lịch sử
            events.addAll(grouped.getOrDefault(step, new ArrayList<>()));

            // Nếu bước là INTERVIEW thì lấy thêm lịch phỏng vấn
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

            timeline.add(
                    TimelineEventResponse.builder()
                            .stepOrder(i + 1)
                            .status(step)
                            .events(events)   // chứa cả history + interview
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
