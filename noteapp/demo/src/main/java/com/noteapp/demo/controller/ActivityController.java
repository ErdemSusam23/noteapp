package com.noteapp.demo.controller;

import com.noteapp.demo.dto.ActivityRequest;
import com.noteapp.demo.dto.ActivityResponse;
import com.noteapp.demo.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/api/activities")
    public ResponseEntity<ActivityResponse> createActivity(@Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.createActivity(request));
    }

    @GetMapping("/api/activities")
    public ResponseEntity<Page<ActivityResponse>> getUserActivities(Pageable pageable) {
        return ResponseEntity.ok(activityService.getUserActivities(pageable));
    }

    @GetMapping("/api/activities/{id}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @GetMapping("/api/activities/date-range")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(activityService.getUserActivitiesByDateRange(startDate, endDate));
    }

    @PutMapping("/api/activities/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable Long id, 
            @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.updateActivity(id, request));
    }

    @DeleteMapping("/api/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
