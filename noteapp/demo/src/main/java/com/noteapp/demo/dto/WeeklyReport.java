package com.noteapp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyReport {
    private String categoryName;
    private double totalHours;
}
