package com.noteapp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyTotals {
    private String period; // LAST_6_MONTHS
    private Map<String, Double> monthlyTotals; // key: YYYY-MM
    private Double totalDuration; // 6 ay toplamı
}
