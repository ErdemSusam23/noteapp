package com.noteapp.demo.controller;

import com.noteapp.demo.dto.GoalRequest;
import com.noteapp.demo.dto.GoalResponse;
import com.noteapp.demo.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/api/goals")
    public ResponseEntity<GoalResponse> createGoal(@RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.createGoal(request));
    }

    @GetMapping("/api/goals")
    public ResponseEntity<List<GoalResponse>> getUserGoals() {
        return ResponseEntity.ok(goalService.getUserGoals());
    }

    @GetMapping("/api/goals/{id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoal(id));
    }

    @DeleteMapping("/api/goals/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/goals/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }

    @GetMapping("/api/goals/{id}/track")
    public ResponseEntity<GoalResponse> trackGoal(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.trackProgress(id));
    }
}
