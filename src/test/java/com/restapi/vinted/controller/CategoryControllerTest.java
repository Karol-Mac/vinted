package com.restapi.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.service.CategoryService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
@WebMvcTest(controllers = CategoryController.class)
////if we add there - there is no necessary to add tokens to our request's
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
//fixme: tokeny jwt są dodane oddzielnie - nie jako część spring security
//  dlatego adnotacja @AutoConfigureMockMvc na nie nie działa
// w klasie JwtAuthenticationFilter dodałem profilowanie
//  (nie jest uruchamiana przy wykonywaniu testów)
// I dzięki temu teraz działa, ale docelowo fajnie by było to zmienić
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

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
    void givenCategoryDto_whencreateCategory_thenCategoryIsCreated() throws Exception{
        when(categoryService.createCategory(categoryDto)).thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    //hope it's enough for testing validation :(
    @Test
    void givenInvalidCategoryDto_whencreateCategory_thenValidationFailed() throws Exception{
        categoryDto.setName("a");

        ResultActions response = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Name has to be at least 3 characters")));
    }

    @Test
    void whenGetAllCategories_thenReturnListOfCategoryDto() throws Exception{
        List<CategoryDto> categories = List.of(categoryDto,
                                            new CategoryDto(2L, "second cat"));
        when(categoryService.getAllCategories()).thenReturn(categories);

        ResultActions response = mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(categories.size())));
    }

    @Test
    void givenCategoryId_whenGetCategoryById_thenReturnCategoryDto() throws Exception{

        when(categoryService.getCategory(category.getId())).thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(
                get("/api/categories/{categoryId}", category.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(category.getName())));
    }

    @Test
    void givenInvalidCategoryId_whenGetCategoryById_thenThrowResourceNotFoundException() throws Exception{
        category.setId(0L);
        ResourceNotFoundException exception = getException();

        when(categoryService.getCategory(category.getId()))
                            .thenThrow(exception);

        ResultActions response = mockMvc.perform(
                get("/api/categories/{categoryId}", category.getId()));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    void givenCategoryId_whenUpdateCategory_thenCategoryIsUpdated() throws Exception{
        when(categoryService.updateCategory(category.getId(), categoryDto))
                                            .thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(
                put("/api/categories/{categoryId}", category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(category.getName())));
    }

    @Test
    void givenInvalidCategoryId_whenUpdateCategory_thenThrowResourceNotFoundException() throws Exception{
        category.setId(0L);
        var exception = getException();
        when(categoryService.updateCategory(category.getId(), categoryDto)).thenThrow(exception);

        ResultActions response = mockMvc.perform(
                put("/api/categories/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }


    //is method name good?
    @Test
    void givenInvalidCategoryDto_whenUpdateCategory_thenValidationFailed() throws Exception{
        categoryDto.setName("a");

        ResultActions response = mockMvc.perform(
                put("/api/categories/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Name has to be at least 3 characters")));
    }

    @Test
    void givenCategoryId_whenDeleteCategory_thenCategoryIsDeleted() throws Exception{
        String message = "Category successfully deleted!";
        when(categoryService.deleteCategory(category.getId())).thenReturn(message);


        ResultActions response = mockMvc.perform(
                delete("/api/categories/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(content().string(message));
    }

    @Test
    void givenInvalidCategoryId_whenDeleteCategory_thenThrowResourceNotFoundException() throws Exception{
        var exception = getException();
        when(categoryService.deleteCategory(category.getId())).thenThrow(exception);


        ResultActions response = mockMvc.perform(
                delete("/api/categories/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }


    @NotNull
    private ResourceNotFoundException getException(){
        return new ResourceNotFoundException(
                "Category", "id", category.getId());
    }
}