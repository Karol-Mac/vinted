package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClotheRepository clotheRepository;

    private Category category;

    @BeforeEach
    void init(){
        category = Category.builder()
                .name("Test Category")
                .build();
    }
    @AfterEach
    void cleanup() {
        clotheRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void givenCategory_whenSave_thenNewCategoryIsSaved() {

        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory);
        assertThat(savedCategory.getId()).isGreaterThan(0);
        assertEquals(savedCategory.getName(), category.getName());
    }

    @Test
    void whenFindAll_thenListOfCategoriesIsRetrived(){
        Category category2 = Category.builder().name("Category 2").build();
        categoryRepository.saveAll(List.of(category, category2));

        List<Category> categories = categoryRepository.findAll();

        assertEquals(2, categories.size());
        assertTrue(categories.contains(category));
    }

    @Test
    void givenCategoryId_whenFindById_thenCategoryIsRetrived() {
        Category savedCategory = categoryRepository.save(category);

        var foundCategory = categoryRepository.findById(savedCategory.getId());

        assertTrue(foundCategory.isPresent());
        assertEquals(savedCategory, foundCategory.get());
    }

    @Test
    void givenCategory_whenDelete_thenCategoryIsDeleted() {
        Category savedCategory = categoryRepository.save(category);

        categoryRepository.delete(savedCategory);
        var foundCategory = categoryRepository.findById(savedCategory.getId());

        assertFalse(foundCategory.isPresent());
    }
}
