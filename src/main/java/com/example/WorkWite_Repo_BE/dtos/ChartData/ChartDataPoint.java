package com.example.WorkWite_Repo_BE.dtos.ChartData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChartDataPoint {
    private String month;
    private long value;

}
