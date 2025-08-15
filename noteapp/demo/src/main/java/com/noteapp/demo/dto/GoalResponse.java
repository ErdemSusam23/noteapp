package com.noteapp.demo.dto;

import com.noteapp.demo.model.GoalStatus;
import com.noteapp.demo.model.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalResponse {
    private Long id;
    private String title;
    private String description;
    private GoalType type;
    private Double targetHours;
    private Double currentHours;
    private LocalDate startDate;
    private LocalDate endDate;
    private GoalStatus status;
    private Long categoryId;
    private String categoryName;
}
