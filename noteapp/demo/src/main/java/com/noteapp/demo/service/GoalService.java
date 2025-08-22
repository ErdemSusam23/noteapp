package com.noteapp.demo.service;

import com.noteapp.demo.dto.GoalRequest;
import com.noteapp.demo.dto.GoalResponse;
import com.noteapp.demo.model.*;
import com.noteapp.demo.repository.ActivityRepository;
import com.noteapp.demo.repository.CategoryRepository;
import com.noteapp.demo.repository.GoalRepository;
import com.noteapp.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityRepository activityRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public GoalResponse createGoal(GoalRequest request) {
        User user = getCurrentUser();

        LocalDate startDate;
        LocalDate endDate;
        if (request.getType() == GoalType.WEEKLY) {
            // Bu haftanın pazartesi - pazar aralığı
            LocalDate today = LocalDate.now();
            startDate = today.minusDays((today.getDayOfWeek().getValue() + 6) % 7);
            endDate = startDate.plusDays(6);
        } else if (request.getType() == GoalType.MONTHLY) {
            YearMonth ym = YearMonth.now();
            startDate = ym.atDay(1);
            endDate = ym.atEndOfMonth();
        } else { // CUSTOM
            startDate = request.getStartDate();
            endDate = request.getEndDate();
            if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
                throw new RuntimeException("Invalid custom date range");
            }
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            if (!category.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You can only use your own categories");
            }
        }

        Goal goal = Goal.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .targetHours(request.getTargetHours())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        goalRepository.save(goal);
        return mapToResponse(goal, 0.0, GoalStatus.ACTIVE);
    }

    public Page<GoalResponse> getUserGoals(Pageable pageable) {
        User user = getCurrentUser();
        return goalRepository.findByUser(user, pageable)
                .map(this::mapToResponseWithComputedStatus);
    }

    public GoalResponse getGoal(Long id) {
        User user = getCurrentUser();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only view your own goals");
        }
        return mapToResponseWithComputedStatus(goal);
    }

    public void deleteGoal(Long id) {
        User user = getCurrentUser();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own goals");
        }
        goalRepository.delete(goal);
    }

    public GoalResponse updateGoal(Long id, GoalRequest request) {
        User user = getCurrentUser();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own goals");
        }

        if (request.getTitle() != null) goal.setTitle(request.getTitle());
        if (request.getDescription() != null) goal.setDescription(request.getDescription());
        if (request.getTargetHours() != null) goal.setTargetHours(request.getTargetHours());
        if (request.getStartDate() != null) goal.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) goal.setEndDate(request.getEndDate());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            if (!category.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You can only use your own categories");
            }
            goal.setCategory(category);
        }

        goalRepository.save(goal);
        return mapToResponseWithComputedStatus(goal);
    }

    public GoalResponse trackProgress(Long id) {
        User user = getCurrentUser();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only view your own goals");
        }

        Double hours;
        if (goal.getCategory() != null) {
            hours = activityRepository.findByUserAndCategoryAndDateBetween(user, goal.getCategory(), goal.getStartDate(), goal.getEndDate())
                    .stream()
                    .mapToDouble(Activity::getDurationHours)
                    .sum();
        } else {
            hours = activityRepository.findByUserAndDateBetween(user, goal.getStartDate(), goal.getEndDate())
                    .stream()
                    .mapToDouble(Activity::getDurationHours)
                    .sum();
        }

        GoalStatus status;
        LocalDate today = LocalDate.now();
        if (hours >= goal.getTargetHours()) {
            status = GoalStatus.COMPLETED;
        } else if (today.isAfter(goal.getEndDate())) {
            status = GoalStatus.FAILED;
        } else {
            status = GoalStatus.ACTIVE;
        }

        return mapToResponse(goal, hours, status);
    }

    private GoalResponse mapToResponseWithComputedStatus(Goal goal) {
        User user = goal.getUser();
        Double hours;
        if (goal.getCategory() != null) {
            hours = activityRepository.findByUserAndCategoryAndDateBetween(user, goal.getCategory(), goal.getStartDate(), goal.getEndDate())
                    .stream()
                    .mapToDouble(Activity::getDurationHours)
                    .sum();
        } else {
            hours = activityRepository.findByUserAndDateBetween(user, goal.getStartDate(), goal.getEndDate())
                    .stream()
                    .mapToDouble(Activity::getDurationHours)
                    .sum();
        }

        GoalStatus status;
        LocalDate today = LocalDate.now();
        if (hours >= goal.getTargetHours()) {
            status = GoalStatus.COMPLETED;
        } else if (today.isAfter(goal.getEndDate())) {
            status = GoalStatus.FAILED;
        } else {
            status = GoalStatus.ACTIVE;
        }

        return mapToResponse(goal, hours, status);
    }

    private GoalResponse mapToResponse(Goal goal, Double currentHours, GoalStatus status) {
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .type(goal.getType())
                .targetHours(goal.getTargetHours())
                .currentHours(currentHours)
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .status(status)
                .categoryId(goal.getCategory() != null ? goal.getCategory().getId() : null)
                .categoryName(goal.getCategory() != null ? goal.getCategory().getName() : null)
                .build();
    }
}
