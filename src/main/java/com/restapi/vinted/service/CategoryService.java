package com.restapi.vinted.service;

import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.payload.CategoryEdditDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryEdditDto categoryDto);
    CategoryDto getCategory(long categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto updateCategory(long categoryId, CategoryEdditDto categoryDto);
    String deleteCategory(long categoryId);
}
