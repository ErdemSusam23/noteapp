package com.noteapp.demo.service;

import com.noteapp.demo.dto.ActivityRequest;
import com.noteapp.demo.dto.ActivityResponse;
import com.noteapp.demo.model.Activity;
import com.noteapp.demo.model.Category;
import com.noteapp.demo.model.User;
import com.noteapp.demo.repository.ActivityRepository;
import com.noteapp.demo.repository.CategoryRepository;
import com.noteapp.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ActivityResponse createActivity(ActivityRequest request) {
        User user = getCurrentUser();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Kullanıcının sadece kendi kategorilerini kullanabilmesini sağla
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only use your own categories");
        }

        Activity activity = Activity.builder()
                .category(category)
                .user(user)
                .date(request.getDate())
                .durationHours(request.getDurationHours())
                .build();

        activityRepository.save(activity);
        return new ActivityResponse(
                activity.getId(),
                activity.getCategory().getId(),
                activity.getCategory().getName(),
                activity.getDate(),
                activity.getDurationHours(),
                activity.getCreatedAt()
        );
    }

    public Page<ActivityResponse> getUserActivities(Pageable pageable) {
        User user = getCurrentUser();
        return activityRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    public List<ActivityResponse> getUserActivitiesByDateRange(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        return activityRepository.findByUserAndDateBetween(user, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(Long id) {
        User user = getCurrentUser();
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only view your own activities");
        }

        return mapToResponse(activity);
    }

    public ActivityResponse updateActivity(Long id, ActivityRequest request) {
        User user = getCurrentUser();
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own activities");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only use your own categories");
        }

        activity.setCategory(category);
        activity.setDate(request.getDate());
        activity.setDurationHours(request.getDurationHours());

        activityRepository.save(activity);
        return mapToResponse(activity);
    }

    public void deleteActivity(Long id) {
        User user = getCurrentUser();
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own activities");
        }

        activityRepository.delete(activity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getCategory().getId(),
                activity.getCategory().getName(),
                activity.getDate(),
                activity.getDurationHours(),
                activity.getCreatedAt()
        );
    }
}
