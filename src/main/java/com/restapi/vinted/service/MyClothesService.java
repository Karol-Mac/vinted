package com.restapi.vinted.service;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;

public interface MyClothesService {
    ClotheDto createClothe(ClotheDto clotheDto);
    ClotheDto getClotheById(long id);

    ClotheResponse getClothes(int pageNo, int pageSize, String sortBy, String direction);

    ClotheDto updateClothe(long id, ClotheDto clotheDto);
    String deleteClothe(long id);
}
