package com.restapi.vinted.utils;

import com.restapi.vinted.controller.CategoryController;
import com.restapi.vinted.payload.CategoryDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<CategoryDto, EntityModel<CategoryDto>> {


    @Override
    public EntityModel<CategoryDto> toModel(CategoryDto entity){

        var selfLink = linkTo(methodOn(CategoryController.class).getCategoryById(entity.getId())).withSelfRel();
        var allLinks = linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("allCategories");

        return EntityModel.of(entity, selfLink, allLinks);
    }

    @Override
    public CollectionModel<EntityModel<CategoryDto>> toCollectionModel(Iterable<? extends CategoryDto> entities){

        var models = StreamSupport.stream(entities.spliterator(), false)
                                    .map(this::toModel)
                                    .toList();
        var selfLinks = linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel();

        return CollectionModel.of(models, selfLinks);
    }
}
