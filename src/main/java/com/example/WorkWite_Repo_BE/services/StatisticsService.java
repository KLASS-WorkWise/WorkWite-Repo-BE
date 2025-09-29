package com.example.WorkWite_Repo_BE.services;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    /**
     * Trả về danh sách top công ty được ứng viên apply nhiều nhất
     */
    public List<Map<String, Object>> getTopCompaniesByApplications() {
        List<Object[]> results = applicantRepo.findTopCompaniesByApplications();
        List<Map<String, Object>> companies = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> company = new HashMap<>();
            company.put("employerId", row[0]);
            company.put("companyName", row[1]);
            company.put("totalApply", row[2]);
            companies.add(company);
        }
        return companies;
    }
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

    /**
     * Thống kê ứng tuyển: tổng số lượt ứng tuyển và tỉ lệ ứng viên ứng tuyển/số tin đăng
     */
    public Map<String, Object> getApplicationStats() {
        long totalApplications = applicantRepo.count();
        long totalJobPostings = jobRepo.count();
        double applyRate = totalJobPostings > 0 ? (totalApplications * 1.0 / totalJobPostings) : 0;
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplications", totalApplications);
        stats.put("applyRate", applyRate);
        return stats;
    }
}