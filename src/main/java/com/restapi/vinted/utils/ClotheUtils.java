package com.restapi.vinted.utils;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.ClotheRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
public class ClotheUtils {

    private final ClotheRepository clotheRepository;

    public ClotheUtils(ClotheRepository clotheRepository) {
        this.clotheRepository = clotheRepository;
    }


    @Transactional(readOnly = true)
    public boolean isOwner(long clotheId, String email) {
        var clothe = getClotheFromDB(clotheId);
        var allClothes = clotheRepository.findByUserEmail(email);

        return allClothes.contains(clothe);
    }

    public Clothe getClotheFromDB(long id) {
        return clotheRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Clothe", "id", id)
        );
    }

    public ClotheResponse getClotheResponse(int pageNo, int pageSize, Page<Clothe> clothes) {
        ClotheResponse clotheResponse = new ClotheResponse();

        clotheResponse.setClothes(clothes.stream().map(this::mapToDto).toList());
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());
        return clotheResponse;
    }

    public Clothe mapToEntity(ClotheDto clotheDto) {
        return Clothe.builder()
                .name(clotheDto.getName())
                .description(clotheDto.getDescription())
                .price(clotheDto.getPrice())
                .size(clotheDto.getSize())
                .material(clotheDto.getMaterial())
                .images(clotheDto.getImages())
                .isAvailable(clotheDto.isAvailable())
                .views(clotheDto.getViews())
                .category(new Category(clotheDto.getCategoryId()))
                .build();
    }

    public ClotheDto mapToDto(Clothe clothe) {
        var conversations = clothe.getConversations() != null ?
                clothe.getConversations().stream().map(Conversation::getId).toList() :
                new ArrayList<Long>();

        return ClotheDto.builder()
                .id(clothe.getId())
                .name(clothe.getName())
                .description(clothe.getDescription())
                .price(clothe.getPrice())
                .size(clothe.getSize())
                .material(clothe.getMaterial())
                .images(clothe.getImages())
                .views(clothe.getViews())
                .isAvailable(clothe.isAvailable())
                .createdAt(clothe.getCreatedAt())
                .updatedAt(clothe.getUpdatedAt())
                .userId(clothe.getUser().getId())
                .categoryId(clothe.getCategory().getId())
                .conversasations(conversations)
                .build();
    }
}