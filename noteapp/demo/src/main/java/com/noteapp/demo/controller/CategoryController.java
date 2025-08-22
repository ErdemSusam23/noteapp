package com.noteapp.demo.controller;

import com.noteapp.demo.dto.CategoryRequest;
import com.noteapp.demo.dto.CategoryResponse;
import com.noteapp.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/api/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PostMapping("/api/category")
    public ResponseEntity<CategoryResponse> createCategoryAlternative(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping("/api/categories")
    public ResponseEntity<Page<CategoryResponse>> getCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getUserCategories(pageable));
    }

    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}