package com.noteapp.demo.service;

import com.noteapp.demo.model.Activity;
import com.noteapp.demo.model.User;
import com.noteapp.demo.repository.ActivityRepository;
import com.noteapp.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityAnalyticsService {
    
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Belirli bir gün için toplam aktivite süresi
     */
    public Double getDailyActivityDuration(LocalDate date) {
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, date, date);
        return activities.stream()
                .mapToDouble(Activity::getDurationHours)
                .sum();
    }

    /**
     * Belirli bir tarih aralığı için toplam aktivite süresi
     */
    public Double getTotalDurationByPeriod(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        return activities.stream()
                .mapToDouble(Activity::getDurationHours)
                .sum();
    }

    /**
     * Kategori bazında performans analizi
     */
    public Map<String, Double> getCategoryPerformance(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        return activities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getCategory().getName(),
                        Collectors.summingDouble(Activity::getDurationHours)
                ));
    }

    /**
     * Haftalık aktivite özeti (son 7 gün)
     */
    public Map<LocalDate, Double> getWeeklyActivitySummary() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        Map<LocalDate, Double> dailySummary = activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getDate,
                        Collectors.summingDouble(Activity::getDurationHours)
                ));
        
        // Eksik günleri 0.0 ile doldur
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailySummary.putIfAbsent(date, 0.0);
        }
        
        return dailySummary;
    }

    /**
     * Aylık aktivite özeti (son 30 gün)
     */
    public Map<LocalDate, Double> getMonthlyActivitySummary() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        Map<LocalDate, Double> dailySummary = activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getDate,
                        Collectors.summingDouble(Activity::getDurationHours)
                ));
        
        // Eksik günleri 0.0 ile doldur
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailySummary.putIfAbsent(date, 0.0);
        }
        
        return dailySummary;
    }

    /**
     * Mevcut streak (kaç gün üst üste aktivite yapıldı)
     */
    public Integer getCurrentStreak() {
        User user = getCurrentUser();
        LocalDate currentDate = LocalDate.now();
        int streak = 0;
        
        while (true) {
            List<Activity> activities = activityRepository.findByUserAndDateBetween(user, currentDate, currentDate);
            if (activities.isEmpty()) {
                break;
            }
            streak++;
            currentDate = currentDate.minusDays(1);
        }
        
        return streak;
    }

    /**
     * En uzun streak
     */
    public Integer getLongestStreak() {
        User user = getCurrentUser();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(365); // Son 1 yıl
        
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        Map<LocalDate, Boolean> activeDays = activities.stream()
                .collect(Collectors.toMap(
                        Activity::getDate,
                        activity -> true,
                        (existing, replacement) -> existing
                ));
        
        int maxStreak = 0;
        int currentStreak = 0;
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (activeDays.containsKey(date)) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }
        
        return maxStreak;
    }

    /**
     * Ortalama günlük aktivite süresi (son 30 gün)
     */
    public Double getAverageDailyDuration() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        
        Double totalDuration = getTotalDurationByPeriod(startDate, endDate);
        long activeDays = activityRepository.findByUserAndDateBetween(getCurrentUser(), startDate, endDate)
                .stream()
                .map(Activity::getDate)
                .distinct()
                .count();
        
        return activeDays > 0 ? totalDuration / activeDays : 0.0;
    }

    /**
     * En aktif gün (en çok aktivite yapılan gün)
     */
    public LocalDate getMostActiveDay(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        return activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getDate,
                        Collectors.summingDouble(Activity::getDurationHours)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Belirli tarih aralığı için günlük aktivite özeti
     */
    public Map<LocalDate, Double> getCustomDateRangeSummary(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        Map<LocalDate, Double> dailySummary = activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getDate,
                        Collectors.summingDouble(Activity::getDurationHours)
                ));
        
        // Eksik günleri 0.0 ile doldur
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailySummary.putIfAbsent(date, 0.0);
        }
        
        return dailySummary;
    }

    /**
     * Son 6 ay için aylık toplamları (YYYY-MM -> toplam saat) olarak döner.
     * Eksik ayları 0.0 ile doldurur ve en eskiden yeniye doğru sıralar.
     */
    public java.util.Map<String, Double> getLastSixMonthsTotals() {
        java.time.YearMonth currentMonth = java.time.YearMonth.now();
        java.time.YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        User user = getCurrentUser();
        List<Activity> activities = activityRepository.findByUserAndDateBetween(user, startDate, endDate);

        java.util.Map<String, Double> totals = activities.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        a -> java.time.YearMonth.from(a.getDate()).toString(), // YYYY-MM
                        java.util.stream.Collectors.summingDouble(Activity::getDurationHours)
                ));

        java.util.LinkedHashMap<String, Double> ordered = new java.util.LinkedHashMap<>();
        for (java.time.YearMonth m = startMonth; !m.isAfter(currentMonth); m = m.plusMonths(1)) {
            String key = m.toString(); // YYYY-MM
            ordered.put(key, totals.getOrDefault(key, 0.0));
        }
        return ordered;
    }
}
