package com.restapi.vinted.controller;

import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.payload.ErrorDetails;
import com.restapi.vinted.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "view category information")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @Operation(summary = "Create new category",
                    description = "Creates new category. Only admin user can perform this")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category successfully created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })
    @SecurityRequirement(name = "bearerToken")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto){
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }


    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable long categoryId){
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }


    @SecurityRequirement(name = "bearerToken")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }



    @SecurityRequirement(name = "bearerToken")
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long categoryId,
                                                       @RequestBody @Valid CategoryDto categoryDto){

        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryDto));
    }
}
