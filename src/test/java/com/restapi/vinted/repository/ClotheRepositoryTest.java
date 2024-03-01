package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Category;
import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.utils.ClotheSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class ClotheRepositoryTest {

    @Autowired
    private ClotheRepository clotheRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Clothe clothe1;
    private Clothe clothe2;

    @BeforeEach
    public void setUpTestData(){
        clothe1 = Clothe.builder()
                .name("hoodie")
                .description("comfortable hoodie")
                .price(BigDecimal.valueOf(145))
                .size(ClotheSize.M)
                .images(List.of("image1", "image2"))
                .build();

        clothe2 = Clothe.builder()
                .name("T-shirt")
                .description("fancy looking T-shirt")
                .price(BigDecimal.valueOf(27))
                .size(ClotheSize.S)
                .images(List.of("image3.jpg", "image4.png"))
                .build();
    }

    @Test
    public void testSave_ValidClothe(){
        Clothe saved = clotheRepository.save(clothe1);

        assertNotNull(saved);
        assertThat(saved.getId()).isGreaterThan(0);
        assertEquals(saved.getName(), saved.getName());
        assertEquals(saved.getDescription(), saved.getDescription());
        assertEquals(saved.getSize(), clothe1.getSize());
        assertIterableEquals(saved.getImages(), clothe1.getImages());
    }

    @Test
    public void testSave_Null(){
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> clotheRepository.save(null));
    }

    @Test
    public void testFindAll_ClotheList(){
        Clothe saved = clotheRepository.save(clothe1);
        clotheRepository.save(clothe2);
        List<Clothe> clothes = clotheRepository.findAll();

        assertNotNull(clothes);
        assertThat(clothes).isNotEmpty();
        assertThat(clothes).contains(saved);
        assertThat(clothes.size()).isGreaterThan(1);
    }

    @Test
    public void testFindAll_ClotheNotInDB(){
        Clothe saved = clotheRepository.save(clothe1);
        List<Clothe> clothes = clotheRepository.findAll();

        assertThat(clothes).isNotEmpty();
        assertThat(clothes).doesNotContain(clothe2);
        assertThat(clothes).contains(saved);
        assertThat(clothes.size()).isEqualTo(1);
    }

    @Test
    public void testFindById_ValidClotheId(){
        Clothe saved = clotheRepository.save(clothe1);

        Clothe founded = clotheRepository.findById(saved.getId()).get();

        assertEquals(saved, founded);
    }

    @Test
    public void testFindById_InvalidClotheId(){
        Clothe saved = clotheRepository.save(clothe1);
        var founded = clotheRepository.findById(saved.getId()+3);

        assertFalse(founded.isPresent());
    }

    @Test
    void testFindByCategoryId_ValidCategoryId() {
        Category category = Category.builder()
                .name("test cat")
                .build();
        Category savedCategory = categoryRepository.save(category);

        clothe1.setCategory(savedCategory);
        Clothe saved = clotheRepository.save(clothe1);
        var founded = clotheRepository.findByCategoryId(savedCategory.getId(),
                                                                Pageable.unpaged());

        assertNotNull(founded);
        assertTrue(founded.getContent().contains(saved));
    }

    @Test
    void testFindByUserId_() {
        User user = User.builder()
                .name("test")
                .email("test@email.com")
                .password("1234qwert")
                .username("testUsername")
                .build();

        var savedUser = userRepository.save(user);
        Clothe saved = clotheRepository.save(clothe1);
        saved.setUser(savedUser);

        var founded = clotheRepository.findByUserId(savedUser.getId());

        assertNotNull(founded);
        assertEquals(founded, List.of(saved));
        assertTrue(founded.contains(saved));
    }

    @Test
    @Disabled
    void testFindByUserIdPageable() {
        User user = User.builder()
                .name("test")
                .email("test@email.com")
                .password("1234qwert")
                .username("testUsername")
                .build();

        var savedUser = userRepository.save(user);
        Clothe saved = clotheRepository.save(clothe1);
        saved.setUser(savedUser);

        var founded = clotheRepository.findByUserId(savedUser.getId(), Pageable.unpaged());

        assertNotNull(founded);
        assertEquals(founded.toList(), List.of(saved));
        assertTrue(founded.toList().contains(saved));
    }
}