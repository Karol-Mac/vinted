package com.restapi.vinted.service;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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
        user = User.builder().id(2L).email("test@email.com").username(USERNAME)
                .name("test").password("1234qwer")
                .roles(Set.of(new Role(1, "ROLE_USER")))
                .build();

        clothe = Clothe.builder()
                        .id(2L)
                        .name("Newest clothe")
                        .description("clothe for testing")
                        .size(ClotheSize.R38)
                        .images(List.of("image1.jpg", "image2.png"))
                        .price(BigDecimal.valueOf(132.99))
                        .user(user)
                        .build();

        clotheDto = ClotheDto.builder()
                        .id(2L)
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
    void givenClotheDto_whenCreateClothe_thenClotheIsSaved(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                            .thenReturn(Optional.of(user));
        when(modelMapper.map(clotheDto, Clothe.class)).thenReturn(clothe);
        when(clotheRepository.save(clothe)).thenReturn(clothe);
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var saved = clothesServiceimpl.createClothe(clotheDto);

        assertNotNull(saved);
        assertEquals(saved, clotheDto);
        verify(userRepository, times(1)).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(clotheRepository, times(1)).save(clothe);
    }
    @Test
    @WithMockUser(username = USERNAME)
    void givenNullClotheDto_whenCreateClothe_thenIllegalArgumentExceptionIdThrown(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                            .thenReturn(Optional.of(user));
        when(modelMapper.map(null, Clothe.class)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class,
                () -> clothesServiceimpl.createClothe(null));

        verify(userRepository, times(1)).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(modelMapper, times(1)).map(null, Clothe.class);
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
        verify(clotheRepository, never()).save(clothe);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void gicenClotheId_whenGetClotheById_thenClotheIsRetrived(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                        .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of(clothe));
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var foundedClothe = clothesServiceimpl.getClotheById(clothe.getId());

        assertNotNull(foundedClothe);
        assertEquals(foundedClothe, clotheDto);
        verify(userRepository, times(1)).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(clotheRepository, times(1)).findByUserId(user.getId());
        verify(modelMapper, times(1)).map(clothe, ClotheDto.class);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void gicenInvalidClotheId_whenGetClotheById_thenClotheIsRetrived(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of(clothe));

        ApiException apiException = assertThrows(ApiException.class,
                () -> clothesServiceimpl.getClotheById(0L));

        assertEquals(apiException.getStatus(), HttpStatus.UNAUTHORIZED);
        assertEquals(apiException.getMessage(), MyClothesServiceimpl.NOT_OWNER);
        verify(userRepository, times(1)).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(clotheRepository, times(1)).findByUserId(user.getId());
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
    }


    @Test
    @WithMockUser(username = USERNAME)
    //IS THERE "ANTY" METHOD TO THIS?
    // No, paggination params cannot be wrong, they have default value
    void whenGetClothes_thenListOfClothesIsRetrived(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Clothe> page = new PageImpl<>(List.of(clothe), pageable, 1);

        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var clothes = clothesServiceimpl
                .getClothes(0, 10, "name", "asc");

        assertNotNull(clothes);
        assertTrue(clothes.getClothes().contains(clotheDto));
        verify(userRepository, times(1)).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(clotheRepository, times(1)).findByUserId(user.getId(), pageable);
        verify(modelMapper, times(1)).map(clothe, ClotheDto.class);
    }


    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheDtoAndId_whenUpdateClothe_thenClotheIsUpdated(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
    }


}













