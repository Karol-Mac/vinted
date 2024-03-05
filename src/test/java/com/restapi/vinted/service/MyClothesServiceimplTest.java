package com.restapi.vinted.service;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.impl.MyClothesServiceimpl;
import com.restapi.vinted.utils.ClotheSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//todo: dokończyć testy dla tej klasy!
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class MyClothesServiceimplTest {
    @Mock
    private ClotheRepository clotheRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MyClothesServiceimpl clothesServiceimpl;

    private Clothe clothe;
    private User user;
    private ClotheDto clotheDto;

    private static final String USERNAME = "testUsername";

    @BeforeEach
    public void init(){
        user = User.builder().email("test@email.com").username(USERNAME)
                .name("test").password("1234qwer").roles(Set.of(new Role(1, "ROLE_USER")))
                .build();

        clothe = Clothe.builder()
                        .name("Newest clothe")
                        .description("clothe for testing")
                        .size(ClotheSize.R38)
                        .images(List.of("image1.jpg", "image2.png"))
                        .price(BigDecimal.valueOf(132.99))
                        .user(user)
                        .build();

        clotheDto = ClotheDto.builder()
                        .name("Newest clothe")
                        .description("clothe for testing")
                        .size(ClotheSize.R38)
                        .images(List.of("image1.jpg", "image2.png"))
                        .price(BigDecimal.valueOf(132.99))
                        .userId(user.getId())
                        .build();
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testCreateClothe_ValidClotheDto(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                            .thenReturn(Optional.of(user));
        when(modelMapper.map(clotheDto, Clothe.class)).thenReturn(clothe);
        when(clotheRepository.save(clothe)).thenReturn(clothe);
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var saved = clothesServiceimpl.createClothe(clotheDto);

        assertNotNull(saved);
        assertEquals(saved, clotheDto);
        verify(userRepository).findByUsernameOrEmail(anyString(), anyString());
        verify(clotheRepository).save(clothe);
    }
    @Test
    @WithMockUser(username = USERNAME)
    void testCreateClothe_InvalidClotheDto(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                            .thenReturn(Optional.of(user));
        when(modelMapper.map(clotheDto, Clothe.class)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class,
                () -> clothesServiceimpl.createClothe(clotheDto));


        verify(userRepository).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(modelMapper, times(1)).map(clotheDto, Clothe.class);
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
        verify(clotheRepository, never()).save(clothe);
    }



    @Test
    void getClotheById() {
    }

    @Test
    void getClothes() {
    }

    @Test
    void updateClothe() {
    }

    @Test
    void deleteClothe() {
    }
}