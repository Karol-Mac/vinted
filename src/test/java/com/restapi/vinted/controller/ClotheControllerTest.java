package com.restapi.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.service.ClotheService;
import com.restapi.vinted.utils.ClotheSize;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ClotheControllerTest {

    private static final String BASE_URL = "/api/clothes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClotheService clotheService;

    private ClotheDto clotheDto1;

    @BeforeEach
    void init(){
        clotheDto1 = ClotheDto.builder()
                .name("hoodie")
                .id(1L)
                .description("comfortable hoodie")
                .price(BigDecimal.valueOf(145))
                .size(ClotheSize.M)
                .images(List.of("image1", "image2"))
                .build();
    }

    @Test
    void givenCategoryIdWithDefaultParams_whenAllClothesFromCategory_thenClotheResponseIsRetrived() throws Exception{
        int pageSize = Integer.parseInt(Constant.PAGE_SIZE_LARGE);
        int pageNo = Integer.parseInt(Constant.PAGE_NO);

        ClotheResponse clotheResponse = new ClotheResponse();
        clotheResponse.setClothes(List.of(clotheDto1));
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setTotalPages(1);
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setLast(true);

        when(clotheService.getClothesRelatedToCategory(1, pageNo, pageSize, "id", "asc"))
                .thenReturn(clotheResponse);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpectAll(
                        status().isOk(),
                        jsonPath("$.clothes.size()", is(clotheResponse.getClothes().size()))
                    );

        ClotheResponse result = objectMapper.readValue(response.andReturn().getResponse().getContentAsString(),
                                                                                        ClotheResponse.class);
        assertEquals(result, clotheResponse);
        assertEquals(result.getClothes(), clotheResponse.getClothes());
    }

    @Test
    void givenCategoryIdWithCustomParams_whenAllClothesFromCategory_thenClotheResponseIsRetrived() throws Exception{
        int pageSize = 9;
        int pageNo = 2;
        String sortBy = "price";
        String direction = "dsc";

        ClotheResponse clotheResponse = new ClotheResponse();
        clotheResponse.setClothes(List.of());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setTotalPages(1);
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setLast(true);

        when(clotheService.getClothesRelatedToCategory(1, pageNo, pageSize, sortBy, direction))
                .thenReturn(clotheResponse);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "1")
                        .param("pageSize", ""+pageSize)
                        .param("pageNo", ""+pageNo)
                        .param("sortBy", sortBy)
                        .param("direction", direction)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        ClotheResponse result = objectMapper.readValue(response.andReturn().getResponse().getContentAsString(),
                                                                                        ClotheResponse.class);
        assertEquals(result, clotheResponse);
        assertEquals(result.getClothes(), clotheResponse.getClothes());
    }

    @Test
    void givenInvalidCategoryId_whenAllClothesFromCategory_thenResourceNotFoundExceptionIsThrown() throws Exception{
        int pageSize = Integer.parseInt(Constant.PAGE_SIZE_LARGE);
        int pageNo = Integer.parseInt(Constant.PAGE_NO);

        var exception = getException("Category", 0);
        when(clotheService.getClothesRelatedToCategory(0, pageNo, pageSize, "id", "asc"))
                .thenThrow(exception);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    void givenCategoryIdAndClotheId_whenGetClotheByCategory_thenClotheDtoIsRetrived() throws Exception{
        when(clotheService.getClotheByCategory(1, clotheDto1.getId()))
                .thenReturn(clotheDto1);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "1")
                .param("clotheId", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());
        ClotheDto result = objectMapper.readValue(response.andReturn().getResponse().getContentAsString(),
                ClotheDto.class);
        assertEquals(result, clotheDto1);
    }

    @Test
    void givenInvalidCategoryId_whenGetClotheByCategory_thenResourceNotFoundExceptionIsThrown() throws Exception{
        var exception = getException("Category", 0);
        when(clotheService.getClotheByCategory(0, clotheDto1.getId()))
                .thenThrow(exception);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "0")
                .param("clotheId", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    void givenInvalidClotheId_whenGetClotheByCategory_thenResourceNotFoundExceptionIsThrown() throws Exception{
        clotheDto1.setId(0L);
        var exception = getException("Clothe", 0);
        when(clotheService.getClotheByCategory(1, clotheDto1.getId())).thenThrow(exception);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "1")
                .param("clotheId", "0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    void givenClotheNotInCategory_whenGetClotheByCategory_thenApiExceptionIsThrown() throws Exception{
        var exception = new ApiException(HttpStatus.BAD_REQUEST, "Clothe does not belong to this category");
        when(clotheService.getClotheByCategory(1, clotheDto1.getId())).thenThrow(exception);

        ResultActions response = mockMvc.perform(get(BASE_URL)
                .param("categoryId", "1")
                .param("clotheId", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @NotNull
    private ResourceNotFoundException getException(String resource, long id){
        return new ResourceNotFoundException(
                resource, "id", id);
    }
}