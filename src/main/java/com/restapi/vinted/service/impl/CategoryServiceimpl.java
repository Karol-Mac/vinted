package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {

    CategoryRepository categoryRepository;
    ModelMapper mapper;

    public CategoryServiceimpl(CategoryRepository categoryRepository,
                               ModelMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = mapCategoryToEntity(categoryDto);

        Category savedCategory = categoryRepository.save(category);

        return mapCategoryToDto(savedCategory);
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = getCategoryFromDB(categoryId);

        return mapCategoryToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(this::mapCategoryToDto).toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) {
        Category category = getCategoryFromDB(categoryId);
        category.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(category);

        return mapCategoryToDto(savedCategory);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(long categoryId) {
        Category category = getCategoryFromDB(categoryId);

        categoryRepository.delete(category);

        return "Category successfully deleted!";
    }

    private Category getCategoryFromDB(long id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", id)
        );
    }


    private Category mapCategoryToEntity(CategoryDto categoryDto){
        return mapper.map(categoryDto, Category.class);
    }

    private CategoryDto mapCategoryToDto(Category category){
        return mapper.map(category, CategoryDto.class);
    }
}
