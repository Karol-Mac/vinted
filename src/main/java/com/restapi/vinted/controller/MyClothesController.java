package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.service.MyClothesService;
import com.restapi.vinted.utils.Constant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/myclothes")
public class MyClothesController {
    private final MyClothesService myClothesService;

    public MyClothesController(MyClothesService myClothesService) {
        this.myClothesService = myClothesService;
    }

    //add new clothe
    @PostMapping
    public ResponseEntity<ClotheDto> createClothe(@RequestBody @Valid ClotheDto clotheDto) {
        return new ResponseEntity<>(myClothesService.createClothe(clotheDto), HttpStatus.CREATED);
    }

    //get all clothes with request parameters
    @GetMapping
    public ResponseEntity<ClotheResponse> getAllClothes(
            @RequestParam(required = false, defaultValue = Constant.PAGE_NO) int pageNo,
            @RequestParam(required = false, defaultValue = Constant.PAGE_SIZE_SMALL) int pageSize,
            @RequestParam(required = false, defaultValue = Constant.SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = Constant.DIRECTION) String direction) {

        return ResponseEntity.ok(myClothesService.getClothes(pageNo, pageSize, sortBy, direction));
    }

    //get exact clothe
    @GetMapping("/{id}")
    public ResponseEntity<ClotheDto> getClothe(@PathVariable long id){
        return ResponseEntity.ok(myClothesService.getClotheById(id));
    }

    //upgrade clothe information
    @PutMapping("/{id}")
    public ResponseEntity<ClotheDto> updateClothe(@PathVariable long id,
                                                  @RequestBody @Valid ClotheDto clotheDto){
        return ResponseEntity.ok(myClothesService.updateClothe(id, clotheDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClothe(@PathVariable long id){
        return ResponseEntity.ok(myClothesService.deleteClothe(id));
    }
}
