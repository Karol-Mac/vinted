package com.restapi.vinted.controller;

import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.service.CategoryService;
import com.restapi.vinted.utils.CategoryModelAssembler;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;

    public CategoryController(CategoryService categoryService, CategoryModelAssembler assembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto){
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoryDto>>> getAllCategories(){
        var categories = categoryService.getAllCategories();
        return ResponseEntity.ok(assembler.toCollectionModel(categories));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> getCategoryById(@PathVariable long categoryId){
        var category = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(assembler.toModel(category));
    }


    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long categoryId,
                                                       @RequestBody @Valid CategoryDto categoryDto){

        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryDto));
    }


}