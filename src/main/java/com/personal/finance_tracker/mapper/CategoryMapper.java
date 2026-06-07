package com.personal.finance_tracker.mapper;

import com.personal.finance_tracker.dto.category.CategoryResponseDTO;
import com.personal.finance_tracker.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toCategoryResponseDTO(Category category);

    List<CategoryResponseDTO> toCategoryResponseDTOList(List<Category> categories);
}
