package com.noteapp.demo.controller;

import com.noteapp.demo.dto.ActivityTrends;
import com.noteapp.demo.dto.DashboardSummary;
import com.noteapp.demo.service.ActivityAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final ActivityAnalyticsService analyticsService;

    /**
     * Dashboard ana özet bilgileri
     */
    @GetMapping("/api/dashboard/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.minusDays(29);
        
        DashboardSummary summary = DashboardSummary.builder()
                .todayDuration(analyticsService.getDailyActivityDuration(today))
                .weekTotalDuration(analyticsService.getTotalDurationByPeriod(weekStart, today))
                .monthTotalDuration(analyticsService.getTotalDurationByPeriod(monthStart, today))
                .currentStreak(analyticsService.getCurrentStreak())
                .longestStreak(analyticsService.getLongestStreak())
                .averageDailyDuration(analyticsService.getAverageDailyDuration())
                .mostActiveDay(analyticsService.getMostActiveDay(monthStart, today))
                .categoryPerformance(analyticsService.getCategoryPerformance(monthStart, today))
                .weeklyActivitySummary(analyticsService.getWeeklyActivitySummary())
                .build();
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Haftalık trend analizi
     */
    @GetMapping("/api/dashboard/trends/weekly")
    public ResponseEntity<ActivityTrends> getWeeklyTrends() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        Map<LocalDate, Double> weeklyData = analyticsService.getWeeklyActivitySummary();
        Double totalDuration = weeklyData.values().stream().mapToDouble(Double::doubleValue).sum();
        Double averageDuration = totalDuration / 7.0;
        
        LocalDate peakDay = weeklyData.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        Double peakValue = peakDay != null ? weeklyData.get(peakDay) : 0.0;
        
        long activeDays = weeklyData.values().stream().filter(duration -> duration > 0).count();
        
        ActivityTrends trends = ActivityTrends.builder()
                .period("WEEKLY")
                .dailyTrends(weeklyData)
                .totalDuration(totalDuration)
                .averageDuration(averageDuration)
                .peakDay(peakDay)
                .peakValue(peakValue)
                .activeDays((int) activeDays)
                .totalDays(7)
                .completionRate(activeDays / 7.0)
                .build();
        
        return ResponseEntity.ok(trends);
    }

    /**
     * Aylık trend analizi
     */
    @GetMapping("/api/dashboard/trends/monthly")
    public ResponseEntity<ActivityTrends> getMonthlyTrends() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        
        Map<LocalDate, Double> monthlyData = analyticsService.getMonthlyActivitySummary();
        Double totalDuration = monthlyData.values().stream().mapToDouble(Double::doubleValue).sum();
        Double averageDuration = totalDuration / 30.0;
        
        LocalDate peakDay = monthlyData.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        Double peakValue = peakDay != null ? monthlyData.get(peakDay) : 0.0;
        
        long activeDays = monthlyData.values().stream().filter(duration -> duration > 0).count();
        
        ActivityTrends trends = ActivityTrends.builder()
                .period("MONTHLY")
                .dailyTrends(monthlyData)
                .totalDuration(totalDuration)
                .averageDuration(averageDuration)
                .peakDay(peakDay)
                .peakValue(peakValue)
                .activeDays((int) activeDays)
                .totalDays(30)
                .completionRate(activeDays / 30.0)
                .build();
        
        return ResponseEntity.ok(trends);
    }

    /**
     * Belirli tarih aralığı için trend analizi
     */
    @GetMapping("/api/dashboard/trends/custom")
    public ResponseEntity<ActivityTrends> getCustomTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<LocalDate, Double> customData = analyticsService.getCustomDateRangeSummary(startDate, endDate);
        Double totalDuration = analyticsService.getTotalDurationByPeriod(startDate, endDate);
        
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Double averageDuration = totalDuration / daysBetween;
        
        LocalDate peakDay = analyticsService.getMostActiveDay(startDate, endDate);
        Double peakValue = peakDay != null ? customData.get(peakDay) : 0.0;
        
        long activeDays = customData.values().stream().filter(duration -> duration > 0).count();
        
        ActivityTrends trends = ActivityTrends.builder()
                .period("CUSTOM")
                .dailyTrends(customData)
                .totalDuration(totalDuration)
                .averageDuration(averageDuration)
                .peakDay(peakDay)
                .peakValue(peakValue)
                .activeDays((int) activeDays)
                .totalDays((int) daysBetween)
                .completionRate(activeDays / (double) daysBetween)
                .build();
        
        return ResponseEntity.ok(trends);
    }

    /**
     * Kategori bazında performans
     */
    @GetMapping("/api/dashboard/category-performance")
    public ResponseEntity<Map<String, Double>> getCategoryPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Double> performance = analyticsService.getCategoryPerformance(startDate, endDate);
        return ResponseEntity.ok(performance);
    }

    /**
     * Streak bilgileri
     */
    @GetMapping("/api/dashboard/streaks")
    public ResponseEntity<Map<String, Integer>> getStreakInfo() {
        Map<String, Integer> streaks = Map.of(
                "currentStreak", analyticsService.getCurrentStreak(),
                "longestStreak", analyticsService.getLongestStreak()
        );
        return ResponseEntity.ok(streaks);
    }
}
