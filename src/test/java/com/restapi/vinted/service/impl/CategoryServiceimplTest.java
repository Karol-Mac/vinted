package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

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
    void testGetCategory_ValidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(categoryDto);

        var foundedCat = categoryServiceimpl.getCategory(category.getId());

        assertNotNull(foundedCat);
        assertEquals(foundedCat.getName(), categoryDto.getName());
        assertEquals(foundedCat.getId(), categoryDto.getId());
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(modelMapper, times(1)).map(any(Category.class), eq(CategoryDto.class));
    }

    @Test
    void testGetCategory_InvalidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () ->categoryServiceimpl.getCategory(category.getId()));
        verify(categoryRepository, times(1)).findById(anyLong());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(categoryDto);

        var categories = categoryServiceimpl.getAllCategories();

        assertNotNull(categories);
        assertEquals(categories.size(), 1);
        verify(categoryRepository, times(1)).findAll();
        verify(modelMapper, times(1))
                                        .map(any(Category.class), eq(CategoryDto.class));
    }

    @Test
    void testUpdateCategory_ValidCategoryIdAndCategoryDto() {
        String oldName = "old name";
        String updatedName = "updated name";
        categoryDto.setName(updatedName);
        category.setName(oldName);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        var updatedCategory = categoryServiceimpl.updateCategory(category.getId(), categoryDto);

        assertNotNull(updatedCategory);
        assertEquals(updatedCategory.getName(), updatedName);
        assertNotEquals(updatedCategory.getName(), oldName);
    }

    @Test
    void testUpdateCategory_InvalidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);


        assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.updateCategory(category.getId(), categoryDto));

        verify(categoryRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testDeleteCategory_ValidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        categoryServiceimpl.deleteCategory(category.getId());

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategory_InvalidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.deleteCategory(category.getId()));

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryRepository, never()).delete(any());
    }
}