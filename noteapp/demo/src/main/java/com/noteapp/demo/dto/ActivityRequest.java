package com.noteapp.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
    @NotNull
    private Long categoryId;

    @NotNull
    private LocalDate date;

    @NotNull
    @Positive
    private Double durationHours;
}
