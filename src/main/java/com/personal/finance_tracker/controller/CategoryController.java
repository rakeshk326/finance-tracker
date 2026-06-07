package com.personal.finance_tracker.controller;

import java.util.*;

import com.personal.finance_tracker.dto.category.CategoryResponseDTO;
import com.personal.finance_tracker.dto.category.CreateCategoryRequestDTO;
import com.personal.finance_tracker.dto.category.UpdateCategoryRequestDTO;
import com.personal.finance_tracker.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PostMapping("")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody @Valid CreateCategoryRequestDTO createCategoryRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryRequestDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID id, @RequestBody UpdateCategoryRequestDTO updateCategoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateCategoryRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
