package com.noteapp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyEntryResponse {
    private Long id;
    private String categoryName;
    private LocalDate date;
    private double hours;

    public DailyEntryResponse(Long id, String categoryName, LocalDate date, double hours) {
        this.id = id;
        this.categoryName = categoryName;
        this.date = date;
        this.hours = hours;
    }
}
