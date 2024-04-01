package com.restapi.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.service.MyClothesService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MyClothesControllerTest {
    private static final String BASE_URL = "/api/myclothes";
    private static final String USERNAME = "username";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MyClothesService clothesService;
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
    @WithMockUser(username = USERNAME)
    void givenClotheDto_whencreateClothe_thenClotheIsCreated() throws Exception{
        when(clothesService.createClothe(clotheDto1)).thenReturn(clotheDto1);

        ResultActions response = mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(clotheDto1)));

        response.andExpect(status().isCreated());

        ClotheDto returnedClothe = objectMapper.readValue(response.andReturn()
                                        .getResponse().getContentAsString(), ClotheDto.class);
        assertEquals(returnedClothe, clotheDto1);
        verify(clothesService, times(1)).createClothe(clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheDto_whencreateClothe_thenValidationFailed() throws Exception{
        clotheDto1.setName("je");
        clotheDto1.setPrice(BigDecimal.valueOf(-15.34));
        clotheDto1.setDescription("to short");
        clotheDto1.setImages(List.of("image1", "image2", "image3", "image4", "image5", "image6"));

        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clotheDto1)));

        response.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.name", is(Constant.NAME_VALIDATION_FAILED)),
                        jsonPath("$.description", is(Constant.DESCRIPTION_VALIDATION_FAILED)),
                        jsonPath("$.price", is(Constant.PRICE_VALIDATION_FAILED)),
                        jsonPath("$.images", is(Constant.IMAGES_VALIDATION_FAILED))
                );

        verify(clothesService, never()).createClothe(clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenNullRequestBody_whencreateClothe_thenClotheIsCreated() throws Exception{
        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Failed to read request"))
                );

        verify(clothesService, never()).createClothe(clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenEmptyObject_whencreateClothe_thenValidationFailed() throws Exception{
        String notNull = "must not be null";
        ResultActions response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ClotheDto())));

        response.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.size", is(notNull)),
                        jsonPath("$.name", is(notNull)),
                        jsonPath("$.price", is(notNull)),
                        jsonPath("$.description", is(notNull))
                );

        verify(clothesService, never()).createClothe(clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenDefaultParameters_whenGetAllClothes_thenClotheResponseIsRetrived() throws Exception{
        int pageNo = Integer.parseInt(Constant.PAGE_NO);
        int pageSize = Integer.parseInt(Constant.PAGE_SIZE_SMALL);

        ClotheResponse clotheResponse = new ClotheResponse();
        clotheResponse.setClothes(List.of(clotheDto1));
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(true);
        clotheResponse.setTotalPages(1);
        when(clothesService.getClothes(pageNo, pageSize, Constant.SORT_BY, Constant.DIRECTION)).thenReturn(clotheResponse);
        ResultActions response =  mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        ClotheResponse retrived = objectMapper.readValue(response.andReturn().getResponse()
                                                        .getContentAsString(), ClotheResponse.class);

        assertEquals(retrived, clotheResponse);
        verify(clothesService, times(1)).getClothes(pageNo, pageSize, Constant.SORT_BY, Constant.DIRECTION);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenCustomParameters_whenGetAllClothes_thenClotheResponseIsRetrived() throws Exception{
        int pageNo = 0;
        int pageSize = 8;
        String sortBy = "images";
        String direction = "desc";

        ClotheResponse clotheResponse = new ClotheResponse();
        clotheResponse.setClothes(List.of(clotheDto1));
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(true);
        clotheResponse.setTotalPages(1);
        when(clothesService.getClothes(pageNo, pageSize, sortBy, direction)).thenReturn(clotheResponse);
        ResultActions response =  mockMvc.perform(get(BASE_URL)
                        .param("pageNo", ""+pageNo)
                        .param("pageSize", ""+pageSize)
                        .param("sortBy", sortBy)
                        .param("direction", direction)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isOk());

        ClotheResponse retrived = objectMapper.readValue(response.andReturn()
                .getResponse().getContentAsString(), ClotheResponse.class);

        assertEquals(retrived, clotheResponse);
        verify(clothesService, times(1)).getClothes(pageNo, pageSize, sortBy, direction);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheId_whenGetClothe_thenClotheDtoIsRetrived() throws Exception{
        when(clothesService.getClotheById(clotheDto1.getId())).thenReturn(clotheDto1);

        ResultActions response =  mockMvc.perform(get(BASE_URL+"/{id}", clotheDto1.getId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isOk());
        ClotheDto retrived = objectMapper.readValue(response.andReturn().getResponse()
                                                            .getContentAsString(), ClotheDto.class);
        assertEquals(retrived, clotheDto1);
        verify(clothesService, times(1)).getClotheById(clotheDto1.getId());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheId_whenGetClothe_thenResourceNotFoundExceptionIsThrown() throws Exception{
        clotheDto1.setId(0L);
        var exception = getException("CLothe", clotheDto1.getId());

        when(clothesService.getClotheById(clotheDto1.getId())).thenThrow(exception);

        ResultActions response =  mockMvc.perform(get(BASE_URL+"/{id}", clotheDto1.getId())
                .contentType(MediaType.APPLICATION_JSON)
        );
        response.andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message", is(exception.getMessage())));

        verify(clothesService, times(1)).getClotheById(clotheDto1.getId());
    }

    @Test
    void upgradeClothe(){
    }

    @Test
    void deleteClothe(){
    }

    @NotNull
    private ResourceNotFoundException getException(String resource, long id){
        return new ResourceNotFoundException(
                resource, "id", id);
    }
}