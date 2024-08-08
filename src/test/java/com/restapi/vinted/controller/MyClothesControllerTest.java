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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .images(List.of("image1.jpg", "image2.png"))
                .build();
    }


    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheDtoAndImages_whenCreateClothe_thenClotheIsCreated() throws Exception {
        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(clotheDto1).getBytes());
        MockMultipartFile imageFile1 = new MockMultipartFile("images", "image1.jpg",
                MediaType.IMAGE_PNG_VALUE , new byte[0]);
        MockMultipartFile imageFile2 = new MockMultipartFile("images", "image2.png",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        when(clothesService.createClothe(clotheDto1,
                    List.of(imageFile1, imageFile2))).thenReturn(clotheDto1);

        ResultActions response = mockMvc.perform(multipart( HttpMethod.POST,BASE_URL)
                        .file(imageFile1)
                        .file(imageFile2)
                        .file(clotheFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA));


        response.andExpect(status().isCreated());

        ClotheDto returnedClothe = objectMapper.readValue(response.andReturn()
                .getResponse().getContentAsString(), ClotheDto.class);
        assertEquals(returnedClothe, clotheDto1);
        verify(clothesService, times(1)).createClothe(clotheDto1, List.of(imageFile1, imageFile2));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenImageIsMissing_whencreateClothe_thenBadRequestExceptionIsThrown() throws Exception{
        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(clotheDto1).getBytes());
        ResultActions response = mockMvc.perform(multipart( HttpMethod.POST,BASE_URL)
                .file(clotheFile));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Required part 'images' is not present.")));

        verify(clothesService, never()).createClothe(clotheDto1, List.of());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheDtoMissing_whencreateClothe_thenBadRequestExceptionIsThrown() throws Exception{
        MockMultipartFile imageFile1 = new MockMultipartFile("images", "image1.jpg",
                MediaType.IMAGE_PNG_VALUE , new byte[0]);
        MockMultipartFile imageFile2 = new MockMultipartFile("images", "image2.png",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        ResultActions response = mockMvc.perform(multipart( HttpMethod.POST,BASE_URL)
                .file(imageFile1)
                .file(imageFile2));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Required part 'clothe' is not present.")));

        verify(clothesService, never()).createClothe(clotheDto1, List.of(imageFile1, imageFile2));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenEmptyRequestIsSend_whencreateClothe_thenBadRequestExceptionIsThrown() throws Exception{
        ResultActions response = mockMvc.perform(multipart( HttpMethod.POST,BASE_URL));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Required part 'clothe' is not present.")));

        verify(clothesService, never()).createClothe(any(ClotheDto.class), any(List.class));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheDto_whencreateClothe_thenValidationFailed() throws Exception{
        clotheDto1.setName("je");
        clotheDto1.setPrice(BigDecimal.valueOf(-15.34));
        clotheDto1.setDescription("to short");

        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(clotheDto1).getBytes());
        MockMultipartFile imageFile1 = new MockMultipartFile("images", "image1.jpg",
                MediaType.IMAGE_PNG_VALUE , new byte[0]);
        ResultActions response = mockMvc.perform(multipart( HttpMethod.POST,BASE_URL)
                        .file(clotheFile)
                        .file(imageFile1)
        );

        response.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.name", is(Constant.NAME_VALIDATION_FAILED)),
                        jsonPath("$.description", is(Constant.DESCRIPTION_VALIDATION_FAILED)),
                        jsonPath("$.price", is(Constant.PRICE_VALIDATION_FAILED))
                );

        verify(clothesService, never()).createClothe(clotheDto1, List.of(imageFile1, imageFile1));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenToManyIMages_whenCreateClothe_thenBadRequestExceptionIsThrown() throws Exception {
        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(clotheDto1).getBytes());
        MockMultipartFile imageFile1 = new MockMultipartFile("images", "image1.jpg",
                MediaType.IMAGE_PNG_VALUE , new byte[0]);

        ResultActions response = mockMvc.perform(multipart( HttpMethod.POST,BASE_URL)
                .file(clotheFile)
                .file(imageFile1)
                .file(imageFile1)
                .file(imageFile1)
                .file(imageFile1)
                .file(imageFile1)
                .file(imageFile1)
                .contentType(MediaType.MULTIPART_FORM_DATA));


        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(Constant.IMAGES_VALIDATION_FAILED)));
        verify(clothesService, never()).createClothe(any(ClotheDto.class), any(List.class));
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
    @WithMockUser(username = USERNAME)
    void givenAllParameters_whenUpdateClothe_thenClotheIsUpdated() throws Exception{
        String oldDescription = clotheDto1.getDescription();
        ClotheSize oldSize = clotheDto1.getSize();
        clotheDto1.setSize(ClotheSize.R40);
        clotheDto1.setDescription("updated description");

        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(clotheDto1));
        MockMultipartFile imageFile2 = new MockMultipartFile("newImages", "image2.png",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        List<String> deleteImages = List.of("image1.jpg");
        MockMultipartFile deleteImagesFile = new MockMultipartFile("deletedImages", "deletedImages.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(deleteImages));


        when(clothesService.updateClothe(clotheDto1.getId(), clotheDto1,
                            List.of(imageFile2), deleteImages)).thenReturn(clotheDto1);
        ResultActions response =  mockMvc
                        .perform(multipart(HttpMethod.PUT,BASE_URL+"/{id}", clotheDto1.getId())
                        .file(clotheFile)
                        .file(imageFile2)
                        .file(deleteImagesFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        response.andExpect(status().isOk());

        ClotheDto updatedClothe = objectMapper.readValue(response.andReturn().getResponse()
                                                                .getContentAsString(), ClotheDto.class);

        assertNotEquals(updatedClothe.getDescription(), oldDescription);
        assertNotEquals(updatedClothe.getSize(), oldSize);
        assertEquals(updatedClothe, clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenRequiredParameters_whenUpdateClothe_thenClotheIsUpdated() throws Exception{
        String oldDescription = clotheDto1.getDescription();
        ClotheSize oldSize = clotheDto1.getSize();
        clotheDto1.setSize(ClotheSize.R40);
        clotheDto1.setDescription("updated description");

        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(clotheDto1));


        when(clothesService.updateClothe(clotheDto1.getId(), clotheDto1, null, null)).thenReturn(clotheDto1);
        ResultActions response =  mockMvc
                .perform(multipart(HttpMethod.PUT,BASE_URL+"/{id}", clotheDto1.getId())
                        .file(clotheFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        response.andExpect(status().isOk());

        ClotheDto updatedClothe = objectMapper.readValue(response.andReturn().getResponse()
                .getContentAsString(), ClotheDto.class);

        assertNotEquals(updatedClothe.getDescription(), oldDescription);
        assertNotEquals(updatedClothe.getSize(), oldSize);
        assertEquals(updatedClothe, clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenNewImage_whenUpdateClothe_thenClotheIsUpdated() throws Exception{
        String oldDescription = clotheDto1.getDescription();
        ClotheSize oldSize = clotheDto1.getSize();
        clotheDto1.setSize(ClotheSize.R40);
        clotheDto1.setDescription("updated description");
        clotheDto1.setImages(List.of("image1.jpg", "image2.png", "image2.png"));

        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(clotheDto1));
        MockMultipartFile imageFile2 = new MockMultipartFile("newImages", "image2.png",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        when(clothesService.updateClothe(clotheDto1.getId(), clotheDto1,
                                List.of(imageFile2) , null)).thenReturn(clotheDto1);
        ResultActions response =  mockMvc
                .perform(multipart(HttpMethod.PUT, BASE_URL+"/{id}", clotheDto1.getId())
                        .file(clotheFile)
                        .file(imageFile2)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        response.andExpect(status().isOk());

        ClotheDto updatedClothe = objectMapper.readValue(response.andReturn().getResponse()
                .getContentAsString(), ClotheDto.class);

        assertNotEquals(updatedClothe.getDescription(), oldDescription);
        assertNotEquals(updatedClothe.getSize(), oldSize);
        assertEquals(updatedClothe, clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenDeleteImage_whenUpdateClothe_thenClotheIsUpdated() throws Exception{
        String oldDescription = clotheDto1.getDescription();
        ClotheSize oldSize = clotheDto1.getSize();
        clotheDto1.setSize(ClotheSize.R40);
        clotheDto1.setDescription("updated description");
        clotheDto1.setImages(List.of("image1.jpg"));
        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(clotheDto1));
        List<String> deleteImages = List.of("image2.png");
        MockMultipartFile deleteImagesFile = new MockMultipartFile("deletedImages", "deletedImages.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(deleteImages));

        when(clothesService.updateClothe(clotheDto1.getId(), clotheDto1,
                null , deleteImages)).thenReturn(clotheDto1);
        ResultActions response =  mockMvc
                .perform(multipart(HttpMethod.PUT, BASE_URL+"/{id}", clotheDto1.getId())
                        .file(clotheFile)
                        .file(deleteImagesFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        response.andExpect(status().isOk());
        ClotheDto updatedClothe = objectMapper.readValue(response.andReturn().getResponse()
                .getContentAsString(), ClotheDto.class);
        assertNotEquals(updatedClothe.getDescription(), oldDescription);
        assertNotEquals(updatedClothe.getSize(), oldSize);
        assertEquals(updatedClothe, clotheDto1);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheId_whenUpdateClothe_thenResourceNotFoundExceptionIsThrown() throws Exception{
        clotheDto1.setId(0L);
        var exception = getException("Clothe", clotheDto1.getId());
        when(clothesService.updateClothe(clotheDto1.getId(), clotheDto1, null, null)).thenThrow(exception);
        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(clotheDto1));

        ResultActions response =  mockMvc.perform(multipart(HttpMethod.PUT,BASE_URL+"/{id}", clotheDto1.getId())
                        .file(clotheFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheDto_whenUpdateClothe_thenValidationFailed() throws Exception{
        clotheDto1.setSize(ClotheSize.R40);
        clotheDto1.setDescription("too short");
        clotheDto1.setPrice(BigDecimal.valueOf(-7.23));
        MockMultipartFile clotheFile = new MockMultipartFile("clothe", "clothe.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(clotheDto1));

        ResultActions response =  mockMvc.perform(multipart(HttpMethod.PUT,BASE_URL+"/{id}", clotheDto1.getId())
                .file(clotheFile)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        response.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.price", is(Constant.PRICE_VALIDATION_FAILED)),
                        jsonPath("$.description", is(Constant.DESCRIPTION_VALIDATION_FAILED))
                );

        verify(clothesService, never()).updateClothe(clotheDto1.getId(), clotheDto1, null, null);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheId_whenDeleteClothe_thenClotheIsDeleted() throws Exception{
        String message = "Clothe deleted successfully!";
        when(clothesService.deleteClothe(clotheDto1.getId())).thenReturn(message);

        ResultActions response =  mockMvc.perform(delete(BASE_URL+"/{id}", clotheDto1.getId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isOk())
                .andExpect(content().string(message));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheId_whenDeleteClothe_thenResourceNotFoundExceptionIsThrown() throws Exception{
        clotheDto1.setId(0L);
        var exception = getException("Clothe", clotheDto1.getId());
        when(clothesService.deleteClothe(clotheDto1.getId())).thenThrow(exception);

        ResultActions response =  mockMvc.perform(delete(BASE_URL+"/{id}", clotheDto1.getId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @NotNull
    private ResourceNotFoundException getException(String resource, long id){
        return new ResourceNotFoundException(
                resource, "id", id);
    }
}