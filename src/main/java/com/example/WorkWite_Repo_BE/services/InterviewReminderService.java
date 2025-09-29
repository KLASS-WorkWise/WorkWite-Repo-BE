// InterviewReminderService.java
package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.entities.Applicant;
import com.example.WorkWite_Repo_BE.entities.InterviewSchedule;
import com.example.WorkWite_Repo_BE.helpers.EmailTemplateHelper;
import com.example.WorkWite_Repo_BE.repositories.InterviewScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("dev")  // chỉ chạy khi active profile là dev
public class InterviewReminderService {

    private final InterviewScheduleRepository interviewScheduleRepository;
    private final EmailService emailService;
    private final EmailTemplateHelper emailTemplateHelper;

    // Chạy mỗi 30 giây
    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void sendInterviewRemindersEvery30s() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now;
        LocalDateTime end = now.plusMinutes(5); // chỉ lấy schedule 5 phút tới

        List<InterviewSchedule> schedules = interviewScheduleRepository.findByScheduledAtBetween(start, end);

        for (InterviewSchedule schedule : schedules) {
            if (schedule.isReminderSent()) continue;

            Applicant applicant = schedule.getApplicant();
            String candidateEmail = applicant.getCandidate().getUser().getEmail();
            String candidateName = applicant.getResume() != null ? applicant.getResume().getFullName() : "Ứng viên";
            String jobTitle = applicant.getJobPosting().getTitle();

            String subject = "🔔 Nhắc nhở phỏng vấn (DEV TEST) - " + jobTitle;
            String content = emailTemplateHelper.buildInterviewReminderEmail(
                    candidateName, jobTitle, schedule.getScheduledAt(), schedule.getLocation()
            );

            emailService.sendEmail(candidateEmail, subject, content);

            schedule.setReminderSent(true); // đánh dấu đã gửi
            interviewScheduleRepository.save(schedule);
        }
    }

//    private final InterviewScheduleRepository interviewScheduleRepository;
//    private final EmailService emailService;
//    private final EmailTemplateHelper emailTemplateHelper;
////             5 phút 1 lần (dành cho test)
////          interview.reminder.cron=0 */5 * * * ?
////            # 8h sáng hàng ngày (production)
////            interview.reminder.cron=0 0 8 * * ?
//    // Chạy lúc 8h sáng mỗi ngày
//    @Scheduled(cron = "0 */5 * * * ?")
//    @Transactional
//    public void sendInterviewReminders() {
//        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(0).withMinute(0);
//        LocalDateTime end = start.plusDays(1);
////        LocalDateTime start = LocalDateTime.now();   // test: bắt đầu từ hiện tại
////        LocalDateTime end = start.plusDays(1);
//
//        List<InterviewSchedule> schedules = interviewScheduleRepository.findByScheduledAtBetween(start, end);
//
//        for (InterviewSchedule schedule : schedules) {
//            if (schedule.isReminderSent()) continue; // tránh gửi lại nhiều lần
//
//            Applicant applicant = schedule.getApplicant();
//            String candidateEmail = applicant.getCandidate().getUser().getEmail();
//            String candidateName = applicant.getResume() != null ? applicant.getResume().getFullName() : "Ứng viên";
//            String jobTitle = applicant.getJobPosting().getTitle();
//
//            String subject = "Nhắc nhở phỏng vấn cho vị trí " + jobTitle;
//            String content = emailTemplateHelper.buildInterviewReminderEmail(
//                    candidateName, jobTitle, schedule.getScheduledAt(), schedule.getLocation()
//            );
//
//            emailService.sendEmail(candidateEmail, subject, content);
//
//            schedule.setReminderSent(true);
//            interviewScheduleRepository.save(schedule);
//        }
//    }
}
