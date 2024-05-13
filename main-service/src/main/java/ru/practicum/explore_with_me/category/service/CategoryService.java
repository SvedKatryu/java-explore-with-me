package ru.practicum.explore_with_me.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.dto.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDTO);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto);

    CategoryDto getCategory(Long catId);

    List<CategoryDto> getAllCategories(PageRequest pageRequest);
}
