package com.restapi.vinted.controller;

import com.restapi.vinted.service.ClotheService;
import com.restapi.vinted.utils.Constant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/clothes")
public class ClotheController {
    private final ClotheService clotheService;

    public ClotheController(ClotheService clotheService) {
        this.clotheService = clotheService;
    }

    @GetMapping
    public ResponseEntity<?> getAllClothesFromCategory(
                                        @RequestParam Long categoryId,
                                        @RequestParam(required = false) Long clotheId,
                                        @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
                                        @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_LARGE) int pageSize,
                                        @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
                                        @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction){

        return Objects.isNull(clotheId) ?
                ResponseEntity.ok(clotheService.getClothesRelatedToCategory(categoryId, pageNo, pageSize, sortBy, direction)) :
                ResponseEntity.ok(clotheService.getClotheByCategory(categoryId, clotheId));
    }
}
