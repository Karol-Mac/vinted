package com.restapi.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.vinted.entity.Category;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.service.CategoryService;
import com.restapi.vinted.utils.Constant;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private final static String ADMIN = "admin";
    private final static String USER = "user";
    private final static String ACCESS_DENIED = "Access Denied";
    private final static String BASE_URL = "/api/categories";

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
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserAndCategoryDto_whencreateCategory_thenCategoryIsCreated() throws Exception{
        when(categoryService.createCategory(categoryDto)).thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    @WithMockUser(roles = "USER", username = USER)
    void givenUserAndCategoryDto_whencreateCategory_thenAccessDeniedExceptionIsThrown() throws Exception{

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(ACCESS_DENIED)));

        verify(categoryService, never()).createCategory(categoryDto);
    }


    @Test
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserAndInvalidCategoryDto_whencreateCategory_thenValidationFailed() throws Exception{
        categoryDto.setName("a");

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is(Constant.NAME_VALIDATION_FAILED)));

        verify(categoryService, never()).createCategory(categoryDto);
    }

    @Test
    @WithMockUser(roles = "USER", username = USER)
    void givenUserAndInvalidCategoryDto_whencreateCategory_thenValidationFailed() throws Exception{
        categoryDto.setName("a");

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name",is(Constant.NAME_VALIDATION_FAILED)));

        verify(categoryService, never()).createCategory(categoryDto);
    }

    @Test
    void whenGetAllCategories_thenReturnListOfCategoryDto() throws Exception{
        List<CategoryDto> categories = List.of(categoryDto,
                                            new CategoryDto(2L, "second cat"));
        when(categoryService.getAllCategories()).thenReturn(categories);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(categories.size())));
    }

    @Test
    void givenCategoryId_whenGetCategoryById_thenReturnCategoryDto() throws Exception{

        when(categoryService.getCategory(category.getId())).thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(
                get(BASE_URL+"/{categoryId}", category.getId())
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
                get(BASE_URL+"/{categoryId}", category.getId()));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserAndCategoryId_whenUpdateCategory_thenCategoryIsUpdated() throws Exception{
        when(categoryService.updateCategory(category.getId(), categoryDto))
                                            .thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(
                put(BASE_URL+"/{categoryId}", category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(category.getName())));
    }

    @Test
    @WithMockUser(roles = "USER", username = USER)
    void givenUserAndCategoryId_whenUpdateCategory_thenAccessDeniedExceptionIsThrown() throws Exception{

        ResultActions response = mockMvc.perform(
                put(BASE_URL+"/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(ACCESS_DENIED)));

        verify(categoryService, never()).updateCategory(category.getId(), categoryDto);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserAndInvalidCategoryId_whenUpdateCategory_thenThrowResourceNotFoundException() throws Exception{
        category.setId(0L);
        var exception = getException();
        when(categoryService.updateCategory(category.getId(), categoryDto)).thenThrow(exception);

        ResultActions response = mockMvc.perform(
                put(BASE_URL+"/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }


    @Test
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserAndInvalidCategoryDto_whenUpdateCategory_thenValidationFailed() throws Exception{
        categoryDto.setName("a");

        ResultActions response = mockMvc.perform(
                put(BASE_URL+"/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is(Constant.NAME_VALIDATION_FAILED)));

        verify(categoryService, never()).updateCategory(category.getId(), categoryDto);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserAndCategoryId_whenDeleteCategory_thenCategoryIsDeleted() throws Exception{
        String message = "Category successfully deleted!";
        when(categoryService.deleteCategory(category.getId())).thenReturn(message);


        ResultActions response = mockMvc.perform(
                delete(BASE_URL+"/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(content().string(message));
    }

    @Test
    @WithMockUser(roles = "USER", username = USER)
    void givenUserCategoryId_whenDeleteCategory_thenAccessDeniedExceptionIsThrown() throws Exception{

        ResultActions response = mockMvc.perform(
                delete(BASE_URL+"/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(ACCESS_DENIED)));

        verify(categoryService, never()).deleteCategory(category.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = ADMIN)
    void givenAdminUserInvalidCategoryId_whenDeleteCategory_thenThrowResourceNotFoundException() throws Exception{
        var exception = getException();
        when(categoryService.deleteCategory(category.getId())).thenThrow(exception);


        ResultActions response = mockMvc.perform(
                delete(BASE_URL+"/{categoryId}", category.getId())
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