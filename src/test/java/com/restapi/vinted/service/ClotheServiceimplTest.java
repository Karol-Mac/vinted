package com.restapi.vinted.service;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.service.impl.ClotheServiceimpl;
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

    private Clothe clothe;
    private Category category;

    private ClotheDto clotheDto;
    @BeforeEach
    public void init(){
        category = Category.builder().name("tested cat").id(1L).build();

        clothe = Clothe.builder()
                .name("hoodie")
                .id(1L)
                .description("comfortable hoodie")
                .price(BigDecimal.valueOf(145))
                .size(ClotheSize.M)
                .images(List.of("image1", "image2"))
                .category(category)
                .build();

        clotheDto = ClotheDto.builder()
                .name("hoodie")
                .id(1L)
                .description("comfortable hoodie")
                .price(BigDecimal.valueOf(145))
                .size(ClotheSize.M)
                .images(List.of("image1", "image2"))
                .build();
    }

    @Test
    public void givenCategoryId_whenGetClothesRelatedToCategory_thenClotheResponseIsRetrieved() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Clothe> page = new PageImpl<>(List.of(clothe), pageable, 1);
        when(clotheRepository.findByCategoryId(clothe.getId(), pageable)).thenReturn(page);
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        ClotheResponse clotheResponse = clotheServiceimpl
                .getClothesRelatedToCategory(category.getId(), 0, 10, "name", "asc");


        assertNotNull(clotheResponse);
        assertEquals(1, clotheResponse.getClothes().size());
        assertEquals(0, clotheResponse.getPageNo());
        assertEquals(1, clotheResponse.getTotalPages());
        assertEquals(10, clotheResponse.getPageSize());
        assertTrue(clotheResponse.isLast());
        assertTrue(clotheResponse.getClothes().contains(clotheDto));

        verify(clotheRepository, times(1))
                            .findByCategoryId(clothe.getId(), pageable);
        verify(modelMapper, times(1))
                            .map(clothe, ClotheDto.class);
    }

    @Test
    public void givenInvalidCategoryId_whenGetClothesRelatedToCategory_thenResourceNotFoundExceptionIsThrown(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        when(clotheRepository.findByCategoryId(0L, pageable)).thenThrow(ResourceNotFoundException.class);


        assertThrows(ResourceNotFoundException.class,
                () -> clotheServiceimpl.getClothesRelatedToCategory(
                                0L, 0, 10, "name", "asc"));

        verify(clotheRepository, times(1))
                                    .findByCategoryId(0L, pageable);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    public void givenClotheIdAndCategoryId_whenGetClotheByCategory_thenListOfClothesIsRetrieved() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(clotheRepository.findById(clothe.getId())).thenReturn(Optional.of(clothe));
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var clotheDto = clotheServiceimpl.getClotheByCategory(category.getId(), clothe.getId());


        assertNotNull(clotheDto);
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(clotheRepository, times(1)).findById(clothe.getId());
        verify(modelMapper, times(1)).map(clothe, ClotheDto.class);
    }

    @Test
    public void givenInvalidCategoryId_whenGetClotheByCategory_thenResourceNotFoundExceptionIsThrown() {
        when(categoryRepository.findById(0L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> clotheServiceimpl.getClotheByCategory(0L, clothe.getId()));

        verify(categoryRepository, times(1)).findById(0L);
        verify(clotheRepository, never()).findById(clothe.getId());
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
    }

    @Test
    public void givenInvalidClotheId_whenGetClotheByCategory_thenResourceNotFoundExceptionIsThrown() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(clotheRepository.findById(0L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> clotheServiceimpl.getClotheByCategory(category.getId(), 0L));

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(clotheRepository, times(1)).findById(0L);
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
    }


    @Test
    public void givenClotheNotInCategory_whenGetClotheByCategory_thenApiExceptionIsThrown() {
        Category anotherCategory = Category.builder().name("Another Category").id(7L).build();
        clothe.setCategory(anotherCategory);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(clotheRepository.findById(clothe.getId())).thenReturn(Optional.of(clothe));

        assertThrows(ApiException.class,
                () -> clotheServiceimpl.getClotheByCategory(category.getId(), clothe.getId()));

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(clotheRepository, times(1)).findById(clothe.getId());
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
    }
}