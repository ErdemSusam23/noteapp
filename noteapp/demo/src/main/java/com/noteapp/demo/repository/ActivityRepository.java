package com.noteapp.demo.repository;

import com.noteapp.demo.model.Activity;
import com.noteapp.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
