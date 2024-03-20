package com.restapi.vinted.service;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.service.impl.CategoryServiceimpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        whenCategoryRepositor_FindById();
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
        category.setId(0L);
        whenCategoryRepositor_FindById(category.getId());

        var exception = assertThrows(ResourceNotFoundException.class,
                () ->categoryServiceimpl.getCategory(category.getId()));

        assertResourceNotFound(exception);
        verify(categoryRepository, times(1)).findById(category.getId());
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
        String updatedName = "updated name";
        categoryDto.setName(updatedName);
        category.setName("old name");

        whenCategoryRepositor_FindById();
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        var updatedCategory = categoryServiceimpl.updateCategory(category.getId(), categoryDto);

        assertNotNull(updatedCategory);
        assertEquals(updatedCategory.getName(), updatedName);
        verifyMockOperation(times(1), repo -> verify(repo, times(1)).save(category));
        verify(modelMapper, times(1)).map(category, CategoryDto.class);
    }

    @Test
    void givenInvalidCategoryId_whenUpdateCategory_thenResourceNotFoundExceptionIsThrown(){
        category.setId(0L);
        whenCategoryRepositor_FindById(category.getId());


        var exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.updateCategory(category.getId(), categoryDto));

        assertResourceNotFound(exception);
        verifyMockOperation(times(1), repo -> verify(repo, never()).save(category));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void givenCategoryId_whenDeleteCategory_thenCategoryIsDeleted(){
        whenCategoryRepositor_FindById();

        var message = categoryServiceimpl.deleteCategory(category.getId());

        assertEquals(message, "Category successfully deleted!");
        verifyMockOperation(times(1),
                            repo -> verify(repo, times(1)).delete(category));
    }

    @Test
    void givenInalidCategoryId_whenDeleteCategory_thenResourceNotFoundExceptionIsThrown(){
        category.setId(0L);
        whenCategoryRepositor_FindById(category.getId());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryServiceimpl.deleteCategory(category.getId()));

        assertResourceNotFound(exception);
        verifyMockOperation(times(1), repo -> verify(repo, never()).delete(category));
    }

    private void assertResourceNotFound(ResourceNotFoundException exception){
        assertEquals(exception.getMessage(), CATEGORY_NOT_FOUND + category.getId());
    }
    private void verifyMockOperation( VerificationMode verifyCategory, @NotNull Consumer<CategoryRepository> action){
        verify(categoryRepository, verifyCategory).findById(category.getId());
        action.accept(categoryRepository);
    }
    private void whenCategoryRepositor_FindById(){
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
    }
    private void whenCategoryRepositor_FindById(long id){
        when(categoryRepository.findById(id)).thenThrow(new ResourceNotFoundException("Category", "id", id));
    }
}