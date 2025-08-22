package com.noteapp.demo.repository;

import com.noteapp.demo.model.Category;
import com.noteapp.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    Page<Category> findByUser(User user, Pageable pageable);
}
