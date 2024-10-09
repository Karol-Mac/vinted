package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.service.ClotheService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClotheServiceimpl implements ClotheService {

    private final ClotheRepository clotheRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper mapper;

    public ClotheServiceimpl(ClotheRepository clotheRepository,
                             CategoryRepository categoryRepository, ModelMapper mapper) {
        this.clotheRepository = clotheRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize,
                                                        String sortBy, String direction){

        //create Sort, and Page object
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNo, pageSize, sort);

        //create Page<Clothe> with custom DB method
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Page<Clothe> clothes = clotheRepository.findByCategoryId(categoryId, page);

        //Create ClotheResponse - give more info to client
        ClotheResponse clotheResponse = new ClotheResponse();
        clotheResponse.setClothes(new ArrayList<>());

        clotheResponse.setClothes(clothes.stream().map(this::mapToDto).toList());
        clotheResponse.setPageNo(clothes.getNumber());
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());

        return clotheResponse;
    }

    @Override
    public ClotheDto getClotheById(long clotheId) {

        Clothe clothe = clotheRepository.findById(clotheId)
                .orElseThrow( ()-> new ResourceNotFoundException("Clothe", "id", clotheId));

        return mapToDto(clothe);
    }

    private ClotheDto mapToDto(Clothe clothe){
        return mapper.map(clothe, ClotheDto.class);
    }
}
