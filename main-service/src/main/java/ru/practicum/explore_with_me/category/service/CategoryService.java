package ru.practicum.explore_with_me.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDTO);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDTO);

    CategoryDto getCategory(Long catId);

    List<CategoryDto> getAllCategories(PageRequest pageRequest);
}
