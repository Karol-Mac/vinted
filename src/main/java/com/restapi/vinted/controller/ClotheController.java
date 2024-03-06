package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.service.ClotheService;
import com.restapi.vinted.utils.Constant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class ClotheController {
    private final ClotheService clotheService;

    public ClotheController(ClotheService clotheService) {
        this.clotheService = clotheService;
    }

    @GetMapping("/{categoryId}/clothes")
    public ResponseEntity<ClotheResponse> getAllClothesFromCategory(@PathVariable long categoryId,
                                                @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
                                                @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_LARGE) int pageSize,
                                                @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
                                                @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction){
        return ResponseEntity.ok(clotheService.getClothesRelatedToCategory(categoryId, pageNo, pageSize, sortBy, direction));
    }

    @GetMapping("/{categoryId}/clothes/{clotheId}")
    public ResponseEntity<ClotheDto> getClotheByCategory(@PathVariable long categoryId,
                                               @PathVariable long clotheId){
        return ResponseEntity.ok(clotheService.getClotheByCategory(categoryId, clotheId) );
    }
}
