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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//fixme: uszczegółowić sprawdzanie wyjątków - tak jak w metodzie
//                                  gicenInvalidClotheId_whenGetClotheById_thenClotheIsRetrived
//                                  (w klasie MyClothesServiceimplTest)




//fixme: trzeba posprawdzać co się stani, jesli w wartaiw kontrolerów prześlemy nulla
//  bo chyba nie koniecznie ten null dotrze do serwisu...dojdzie obiekt, którego pola to nulle
@ExtendWith(MockitoExtension.class)
class CategoryServiceimplTest {

    private static final String CATEGORY_NOT_FOUND = "Category not found with id = ";

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

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
    void givenCategoryDto_whenCreateCategory_thenCategoryIsSaved(){
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
    void givenCategoryId_whenGetCategory_thenCategoryIsRertived(){
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
    void givenInvalidCateggoryId_whenGetCategory_thenResourceNotFoundExceptionIsThrown(){
        when(categoryRepository.findById(0L)).thenThrow(
                                        new ResourceNotFoundException("Category", "id", 0L));

        var exception = assertThrows(ResourceNotFoundException.class,
                () ->categoryServiceimpl.getCategory(0L));

        assertEquals(exception.getMessage(), CATEGORY_NOT_FOUND + 0L);
        verify(categoryRepository, times(1)).findById(0L);
        verify(modelMapper, never()).map(category, CategoryDto.class);
    }

    @Test
    void whenGetAllCategories_thenListOfCategoriesIsRetrieved(){
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
    void givenCategoryIdAndCategoryDto_whenUpdateCategory_thenCategoryIsUpdated(){
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
    void givenInvalidCategoryId_whenUpdateCategory_thenResourceNotFoundExceptionIsThrown(){
        when(categoryRepository.findById(0L)).thenThrow(
                                                new ResourceNotFoundException("Category", "id", 0L));


        var exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.updateCategory(0L, categoryDto));

        assertEquals(exception.getMessage(), CATEGORY_NOT_FOUND + 0L);
        verify(categoryRepository, times(1)).findById(0L);
        verify(categoryRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void givenCategoryId_whenDeleteCategory_thenCategoryIsDeleted(){
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        categoryServiceimpl.deleteCategory(category.getId());

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void givenInalidCategoryId_whenDeleteCategory_thenResourceNotFoundExceptionIsThrown(){
        when(categoryRepository.findById(0L)).thenThrow(
                                            new ResourceNotFoundException("Category", "id", 0L));

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.deleteCategory(0L));

        assertEquals(exception.getMessage(), CATEGORY_NOT_FOUND + 0L);
        verify(categoryRepository, times(1)).findById(0L);
        verify(categoryRepository, never()).delete(any());
    }
}