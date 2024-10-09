package com.restapi.vinted.service;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;

public interface ClotheService {
    ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize, String sortBy, String direction);

    ClotheDto getClotheById(long clotheId);
}
