package com.noteapp.demo.service;

import com.noteapp.demo.dto.CategoryRequest;
import com.noteapp.demo.dto.CategoryResponse;
import com.noteapp.demo.model.Category;
import com.noteapp.demo.model.User;
import com.noteapp.demo.repository.CategoryRepository;
import com.noteapp.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        User user = getCurrentUser();

        Category category = Category.builder()
                .name(request.getName())
                .user(user)
                .build();

        categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }

    public Page<CategoryResponse> getUserCategories(Pageable pageable) {
        User user = getCurrentUser();
        return categoryRepository.findByUser(user, pageable)
                .map(c -> new CategoryResponse(c.getId(), c.getName()));
    }

    public void deleteCategory(Long id) {
        User user = getCurrentUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own categories");
        }

        categoryRepository.delete(category);
    }

}
