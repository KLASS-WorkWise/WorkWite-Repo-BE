package com.example.WorkWite_Repo_BE.services;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.WorkWite_Repo_BE.dtos.ChartData.ChartDataPoint;
import com.example.WorkWite_Repo_BE.dtos.Stats.OverviewStatsDto;
import com.example.WorkWite_Repo_BE.repositories.ApplicantRepository;
import com.example.WorkWite_Repo_BE.repositories.CandidateJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.EmployersJpaRepository;
import com.example.WorkWite_Repo_BE.repositories.JobPostingRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;

@Service
public class StatisticsService {
    @Autowired
    private UserJpaRepository userRepo;
    @Autowired
    private EmployersJpaRepository employerRepo;
    @Autowired
    private CandidateJpaRepository candidateRepo;
    @Autowired
    private JobPostingRepository jobRepo;
    @Autowired
    private ApplicantRepository applicantRepo;

    public OverviewStatsDto getOverviewStats() {
        OverviewStatsDto dto = new OverviewStatsDto();
        dto.setTotalUsers(userRepo.count());
        dto.setTotalEmployers(employerRepo.countByStatus("APPROVED"));
        dto.setTotalCandidates(candidateRepo.count());
        dto.setTotalJobPostings(jobRepo.count());
        dto.setTotalApplications(applicantRepo.count());
        return dto;
    }

    public List<ChartDataPoint> getUserGrowth(int year) {
        List<Object[]> results = userRepo.countUserByMonth(year);
        List<ChartDataPoint> data = new ArrayList<>();
        for (Object[] row : results) {
            int monthNum = (int) row[0];
            long value = (long) row[1];
            String month = Month.of(monthNum).name().substring(0, 3); // "JAN", "FEB", ...
            data.add(new ChartDataPoint(month, value));
        }
        return data;
    }

    public List<ChartDataPoint> getJobPostingGrowth(int year) {
        List<Object[]> results = jobRepo.countJobPostingByMonth(year);
        List<ChartDataPoint> data = new ArrayList<>();
        for (Object[] row : results) {
            int monthNum = (int) row[0];
            long value = (long) row[1];
            String month = Month.of(monthNum).name().substring(0, 3);
            data.add(new ChartDataPoint(month, value));
        }
        return data;
    }

    public double getConversionRate() {
        long hired = applicantRepo.countByApplicationStatus(com.example.WorkWite_Repo_BE.enums.ApplicationStatus.HIRED);
        long total = applicantRepo.count();
        return total > 0 ? (hired * 100.0 / total) : 0;
    }
}