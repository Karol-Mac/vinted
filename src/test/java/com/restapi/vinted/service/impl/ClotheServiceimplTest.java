package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.utils.ClotheSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClotheServiceimplTest {

    @Mock
    private ClotheRepository clotheRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ClotheServiceimpl clotheServiceimpl;

    private Clothe clothe1;
    private Category category;
    @BeforeEach
    public void init(){
        category = Category.builder().name("tested cat").id(1L).build();

        clothe1 = Clothe.builder()
                .name("hoodie")
                .id(1L)
                .description("comfortable hoodie")
                .price(BigDecimal.valueOf(145))
                .size(ClotheSize.M)
                .images(List.of("image1", "image2"))
                .category(category)
                .build();
    }



    @Test
    public void testGetClothesRelatedToCategory_ValidCategoryId() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Clothe> page = new PageImpl<>(List.of(clothe1), pageable, 1);


        when(clotheRepository.findByCategoryId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Clothe.class), eq(ClotheDto.class))).thenReturn(new ClotheDto());
        ClotheResponse clotheResponse = clotheServiceimpl.getClothesRelatedToCategory(
                                category.getId(), 0, 10, "name", "asc");


        assertNotNull(clotheResponse);
        assertEquals(1, clotheResponse.getClothes().size());
        assertEquals(0, clotheResponse.getPageNo());
        assertEquals(1, clotheResponse.getTotalPages());
        assertEquals(10, clotheResponse.getPageSize());
        assertTrue(clotheResponse.isLast());

        verify(clotheRepository, times(1))
                            .findByCategoryId(anyLong(), any(Pageable.class));
        verify(modelMapper, times(1))
                            .map(any(Clothe.class), eq(ClotheDto.class));
    }

    @Test
    public void testGetClothesRelatedToCategory_InvalidCategoryId() {
        when(clotheRepository.findByCategoryId(anyLong(), any(Pageable.class)))
                .thenThrow(ResourceNotFoundException.class);


        assertThrows(ResourceNotFoundException.class,
                () -> clotheServiceimpl.getClothesRelatedToCategory(
                                category.getId(), 0, 10, "name", "asc"));

        verify(clotheRepository, times(1))
                                    .findByCategoryId(anyLong(), any(PageRequest.class));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    public void testGetClotheByCategory_ValidClotheIdAndCategoryId() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(clotheRepository.findById(anyLong())).thenReturn(Optional.of(clothe1));
        when(modelMapper.map(any(Clothe.class), eq(ClotheDto.class))).thenReturn(new ClotheDto());

        // When
        ClotheDto clotheDto = clotheServiceimpl.getClotheByCategory(category.getId(), clothe1.getId());

        // Then
        assertNotNull(clotheDto);

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(clotheRepository, times(1)).findById(anyLong());
        verify(modelMapper, times(1)).map(any(Clothe.class), eq(ClotheDto.class));
    }

    @Test
    public void testGetClotheByCategory_CategoryNotFound() {
        when(categoryRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> clotheServiceimpl.getClotheByCategory(category.getId(), clothe1.getId()));

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(clotheRepository, never()).findById(anyLong());
        verify(modelMapper, never()).map(any(Clothe.class), eq(ClotheDto.class));
    }

    @Test
    public void testGetClotheByCategory_ClotheNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(clotheRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> clotheServiceimpl.getClotheByCategory(category.getId(), clothe1.getId()));

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(clotheRepository, times(1)).findById(anyLong());
        verify(modelMapper, never()).map(any(Clothe.class), eq(ClotheDto.class));
    }

    @Test
    public void testGetClotheByCategory_ClotheNotBelongToCategory() {
        // Given
        Category anotherCategory = Category.builder()
                            .name("Another Category")
                            .id(7L)
                            .build();
        Clothe clotheInAnotherCategory = Clothe.builder()
                            .name("T-Shirt")
                            .id(7L)
                            .description("comfortable T-shirt")
                            .price(BigDecimal.valueOf(500L))
                            .size(ClotheSize.L)
                            .images(List.of("image2.jpg"))
                            .category(anotherCategory)
                            .build();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(clotheRepository.findById(anyLong())).thenReturn(Optional.of(clotheInAnotherCategory));

        assertThrows(ApiException.class,
                () -> clotheServiceimpl.getClotheByCategory(category.getId(), clotheInAnotherCategory.getId()));

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(clotheRepository, times(1)).findById(anyLong());
        verify(modelMapper, never()).map(any(), any());
    }

}