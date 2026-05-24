package com.revnu.backend.features.categories.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.categories.dto.CategoryRequest;
import com.revnu.backend.features.categories.dto.CategoryResponse;
import com.revnu.backend.features.categories.service.CategoryService;
import com.revnu.backend.shared.exception.ApiResponse;
import com.revnu.backend.shared.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/categories")
@PreAuthorize("hasRole('RESTAURATEUR')")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getCategories(
            Principal principal,
            @RequestParam(required = false) String type) {
        List<CategoryResponse> categories = categoryService.getCategories(principal.getName(), type);
        return ResponseEntity.ok(ResponseUtil.success(categories));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(
            Principal principal,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(principal.getName(), id, request);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(
            Principal principal,
            @PathVariable UUID id) {
        categoryService.deleteCategory(principal.getName(), id);
        return ResponseEntity.ok(ResponseUtil.success(Map.of("deleted", true)));
    }
}
