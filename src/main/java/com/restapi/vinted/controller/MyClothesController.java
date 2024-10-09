package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.service.MyClothesService;
import com.restapi.vinted.utils.Constant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/myclothes")
public class MyClothesController {
    private final MyClothesService myClothesService;

    public MyClothesController(MyClothesService myClothesService) {
        this.myClothesService = myClothesService;
    }

    @GetMapping
    public ResponseEntity<ClotheResponse> getAllClothes(
            @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
            @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_SMALL) int pageSize,
            @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction,
            Principal principal) {

        return ResponseEntity.ok(myClothesService.getMyClothes(pageNo, pageSize, sortBy, direction, principal.getName()));
    }
}
