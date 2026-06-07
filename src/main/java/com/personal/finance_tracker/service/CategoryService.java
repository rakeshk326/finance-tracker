package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.category.CategoryResponseDTO;
import com.personal.finance_tracker.dto.category.CreateCategoryRequestDTO;
import com.personal.finance_tracker.dto.category.UpdateCategoryRequestDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryResponseDTO> getCategories();

    CategoryResponseDTO createCategory(CreateCategoryRequestDTO createCategoryRequestDTO);

    void deleteCategory(UUID id);

    CategoryResponseDTO updateCategory(UUID id, UpdateCategoryRequestDTO updateCategoryRequestDTO);
}