package com.noteapp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyEntryRequest {
    private Long categoryId;
    private LocalDate date;
    private double hours;
}
