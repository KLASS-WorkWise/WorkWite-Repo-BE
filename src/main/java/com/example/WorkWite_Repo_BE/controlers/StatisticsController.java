package com.example.WorkWite_Repo_BE.controlers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.WorkWite_Repo_BE.dtos.Stats.OverviewStatsDto;
import com.example.WorkWite_Repo_BE.services.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    /**
     * API trả về danh sách top công ty được ứng viên apply nhiều nhất
     */
    @GetMapping("/top-companies")
    public ResponseEntity<List<Map<String, Object>>> getTopCompanies() {
        List<Map<String, Object>> companies = statisticsService.getTopCompaniesByApplications();
        return ResponseEntity.ok(companies);
    }
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<OverviewStatsDto> getOverviewStats() {
        OverviewStatsDto stats = statisticsService.getOverviewStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/chart-data")
    public ResponseEntity<Map<String, Object>> getChartData(@RequestParam int year) {
        Map<String, Object> result = new HashMap<>();
        result.put("userGrowth", statisticsService.getUserGrowth(year));
        result.put("jobPosting", statisticsService.getJobPostingGrowth(year));
        result.put("applicationStats", statisticsService.getApplicationStats());
        return ResponseEntity.ok(result);
    }
}