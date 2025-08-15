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
public class ActivityTrends {
    private String period; // WEEKLY, MONTHLY
    private Map<LocalDate, Double> dailyTrends;
    private Double totalDuration;
    private Double averageDuration;
    private LocalDate peakDay;
    private Double peakValue;
    private Integer activeDays;
    private Integer totalDays;
    private Double completionRate; // activeDays / totalDays
}
