package com.noteapp.demo.repository;

import com.noteapp.demo.model.Goal;
import com.noteapp.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
}
