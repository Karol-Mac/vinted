package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.service.MyClothesService;
import com.restapi.vinted.utils.Constant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/myclothes")
public class MyClothesController {
    private final MyClothesService myClothesService;

    public MyClothesController(MyClothesService myClothesService) {
        this.myClothesService = myClothesService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<ClotheDto> createClothe(@RequestPart("clothe") @Valid ClotheDto clotheDto,
                                                  @RequestPart("images") List<MultipartFile> images) {

        return new ResponseEntity<>(myClothesService.createClothe(clotheDto, images), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ClotheResponse> getAllClothes(
            @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
            @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_SMALL) int pageSize,
            @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction) {

        return ResponseEntity.ok(myClothesService.getClothes(pageNo, pageSize, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClotheDto> getClothe(@PathVariable long id){
        return ResponseEntity.ok(myClothesService.getClotheById(id));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<ClotheDto> updateClothe(@PathVariable Long id,
                                                  @RequestPart("clothe") @Valid ClotheDto clotheDto,
                                                  @RequestPart(name = "newImages", required = false) List<MultipartFile> newImages,
                                                  @RequestPart(name = "deletedImages", required = false) List<String> deletedImages) {

        return ResponseEntity.ok(myClothesService.updateClothe(id, clotheDto, newImages, deletedImages));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClothe(@PathVariable long id){
        return ResponseEntity.ok(myClothesService.deleteClothe(id));
    }
}
