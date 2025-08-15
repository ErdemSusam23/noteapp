package com.noteapp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
    private Long categoryId;
    private LocalDate date;
    private Double durationHours;
}
