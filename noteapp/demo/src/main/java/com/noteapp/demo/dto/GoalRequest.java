package com.noteapp.demo.dto;

import com.noteapp.demo.model.GoalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalRequest {
    private String title;
    private String description;
    private GoalType type; // WEEKLY, MONTHLY, CUSTOM
    private Double targetHours;
    private LocalDate startDate; // WEEKLY/MONTHLY için yok sayılabilir
    private LocalDate endDate;   // WEEKLY/MONTHLY için otomatik hesaplanır
    private Long categoryId; // optional
}
