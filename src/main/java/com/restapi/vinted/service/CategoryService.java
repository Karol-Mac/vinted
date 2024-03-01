package com.restapi.vinted.service;

import com.restapi.vinted.payload.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto getCategory(long categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto updateCategory(long categoryId, CategoryDto categoryDto);
    String deleteCategory(long categoryId);
}
