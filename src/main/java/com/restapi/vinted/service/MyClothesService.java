package com.restapi.vinted.service;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyClothesService {
    ClotheDto createClothe(ClotheDto clotheDto, List<MultipartFile> images);
    ClotheDto getClotheById(long id);

    ClotheResponse getClothes(int pageNo, int pageSize, String sortBy, String direction);

    ClotheDto updateClothe(long id, ClotheDto clotheDto,
                           List<MultipartFile> newImages, List<String> deletedImages);
    String deleteClothe(long id);
}
