package com.restapi.vinted.service;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.service.impl.CategoryServiceimpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//fixme: zmienić nazwy testów według metodologi given-when-then
//                                      (więcej na gpt)
//ten sam refactor zrobić rzecz jasna w innych klasach testowych


@ExtendWith({MockitoExtension.class, SpringExtension.class})
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
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto savedCat = categoryServiceimpl.createCategory(categoryDto);

        assertNotNull(savedCat);
        assertEquals(savedCat, categoryDto);
        verify(modelMapper, times(1)).map(categoryDto, Category.class);
        verify(categoryRepository, times(1)).save(category);
        verify(modelMapper, times(1)).map(category, CategoryDto.class);
    }

    @Test
    void testCreateCategory_NullCategoryDto(){
        when(modelMapper.map(null, Category.class))
                .thenThrow(IllegalArgumentException.class);


        assertThrows(IllegalArgumentException.class,
                () ->categoryServiceimpl.createCategory(null));

        verify(modelMapper, times(1)).map(null, Category.class);
        verify(categoryRepository, never()).save(any());
        verify(modelMapper, never()).map(category, CategoryDto.class);
    }

    @Test
    void testGetCategory_ValidCategoryId() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        var foundedCat = categoryServiceimpl.getCategory(category.getId());

        assertNotNull(foundedCat);
        assertEquals(foundedCat.getId(), categoryDto.getId());
        assertEquals(foundedCat, categoryDto);
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(modelMapper, times(1)).map(category, CategoryDto.class);
    }

    @Test
    void testGetCategory_InvalidCategoryId() {
        when(categoryRepository.findById(0L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () ->categoryServiceimpl.getCategory(0L));
        verify(categoryRepository, times(1)).findById(0L);
        verify(modelMapper, never()).map(category, CategoryDto.class);
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        var categories = categoryServiceimpl.getAllCategories();

        assertNotNull(categories);
        assertEquals(categories.size(), 1);
        assertTrue(categories.contains(categoryDto));
        verify(categoryRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(category, CategoryDto.class);
    }

    @Test
    void testUpdateCategory_ValidCategoryIdAndCategoryDto() {
        String oldName = "old name";
        String updatedName = "updated name";
        categoryDto.setName(updatedName);
        category.setName(oldName);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        var updatedCategory = categoryServiceimpl.updateCategory(category.getId(), categoryDto);

        assertNotNull(updatedCategory);
        assertEquals(updatedCategory.getName(), updatedName);
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).save(category);
        verify(modelMapper).map(category, CategoryDto.class);
    }

    @Test
    void testUpdateCategory_InvalidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);


        assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.updateCategory(anyLong(), categoryDto));

        verify(categoryRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testDeleteCategory_ValidCategoryId() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        categoryServiceimpl.deleteCategory(category.getId());

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategory_InvalidCategoryId() {
        when(categoryRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.deleteCategory(0L));

        verify(categoryRepository, times(1)).findById(0L);
        verify(categoryRepository, never()).delete(any());
    }
}