package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if(categoryDto.getName() == null)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Name cannot be null");

        Category category = mapToEntity(categoryDto);

        Category savedCategory = categoryRepository.save(category);

        return mapToDto(savedCategory);
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = getCategoryFromDB(categoryId);

        return mapToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(this::mapToDto).toList();
    }

    @Override
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) {
        Category category = getCategoryFromDB(categoryId);
        category.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(category);

        return mapToDto(savedCategory);
    }

    @Override
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


    private Category mapToEntity(CategoryDto categoryDto){
        return mapper.map(categoryDto, Category.class);
    }

    private CategoryDto mapToDto(Category category){
        return mapper.map(category, CategoryDto.class);
    }

}
