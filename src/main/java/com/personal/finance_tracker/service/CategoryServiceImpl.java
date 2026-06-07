package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.category.CategoryResponseDTO;
import com.personal.finance_tracker.dto.category.CreateCategoryRequestDTO;
import com.personal.finance_tracker.dto.category.UpdateCategoryRequestDTO;
import com.personal.finance_tracker.entity.Category;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.exception.ResourceAlreadyExistsException;
import com.personal.finance_tracker.exception.ResourceNotFoundException;
import com.personal.finance_tracker.mapper.CategoryMapper;
import com.personal.finance_tracker.repository.CategoryRepository;
import com.personal.finance_tracker.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponseDTO> getCategories() {

        UUID userId = SecurityUtil.getCurrentUserId();

        List<Category> categories = categoryRepository.findByDeletedAtIsNullAndUserIdOrDeletedAtIsNullAndUserIdIsNull(userId);
        return categoryMapper.toCategoryResponseDTOList(categories);
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO createCategoryRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setUser(user);
        category.setName(createCategoryRequestDTO.getName().trim());
        category.setType(createCategoryRequestDTO.getType());

        try {
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toCategoryResponseDTO(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID categoryId, UpdateCategoryRequestDTO updateCategoryRequestDTO) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Category category = categoryRepository.findByIdAndUserIdAndDeletedAtIsNull(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (updateCategoryRequestDTO.getName() != null && !updateCategoryRequestDTO.getName().trim().isEmpty()) {
            category.setName(updateCategoryRequestDTO.getName().trim());
        }

        if (updateCategoryRequestDTO.getType() != null) {
            category.setType(updateCategoryRequestDTO.getType());
        }

        try {
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toCategoryResponseDTO(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

    }

    @Override
    public void deleteCategory(UUID categoryId) {

        UUID userId = SecurityUtil.getCurrentUserId();

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

}