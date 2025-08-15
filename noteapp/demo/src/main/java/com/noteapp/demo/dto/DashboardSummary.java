package com.noteapp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSummary {
    private Double todayDuration;
    private Double weekTotalDuration;
    private Double monthTotalDuration;
    private Integer currentStreak;
    private Integer longestStreak;
    private Double averageDailyDuration;
    private LocalDate mostActiveDay;
    private Map<String, Double> categoryPerformance;
    private Map<LocalDate, Double> weeklyActivitySummary;
}
