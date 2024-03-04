package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceimplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceimpl categoryServiceimpl;

    private Category category;

    private CategoryDto categoryDto;


    @BeforeEach
    public void init(){
        category = Category.builder()
                .id(1L)
                .name("Tested cat")
                .build();

        categoryDto = new CategoryDto(category.getId(), category.getName());
    }

    @Test
    void testCreateCategory_ValidCategoryDto(){
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(categoryDto);

        CategoryDto savedCat = categoryServiceimpl.createCategory(categoryDto);

        assertNotNull(savedCat);
        assertEquals(savedCat.getName(), categoryDto.getName());
        verify(modelMapper, times(1)).map(any(CategoryDto.class), eq(Category.class));
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_NullCategoryDto(){
        when(modelMapper.map(any(CategoryDto.class), eq(Category.class)))
                .thenThrow(IllegalArgumentException.class);


        assertThrows(IllegalArgumentException.class,
                () ->categoryServiceimpl.createCategory(categoryDto));

        verify(modelMapper, times(1)).map(any(CategoryDto.class), eq(Category.class));
        verify(categoryRepository, never()).save(any(Category.class));
        verify(modelMapper, never()).map(any(Category.class), eq(CategoryDto.class));
    }


    @Test
    void getCategory() {
    }

    @Test
    void getAllCategories() {
    }

    @Test
    void updateCategory() {
    }

    @Test
    void deleteCategory() {
    }
}