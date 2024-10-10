package com.restapi.vinted.service;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClothesService {

    ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize, String sortBy, String direction);

    ClotheDto getClotheById(long clotheId, String email);

    ClotheDto addClothe(ClotheDto clotheDto, List<MultipartFile> images, String email);

    ClotheResponse getMyClothes(int pageNo, int pageSize, String sortBy, String direction, String email);

    ClotheDto updateClothe(long id, ClotheDto clotheDto,
                           List<MultipartFile> newImages, List<String> deletedImages, String email);
    String deleteClothe(long id, String email);
}
