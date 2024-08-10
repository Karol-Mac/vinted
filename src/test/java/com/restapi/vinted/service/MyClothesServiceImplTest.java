package com.restapi.vinted.service;


import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.impl.MyClothesServiceimpl;
import com.restapi.vinted.utils.ClotheSize;
import com.restapi.vinted.utils.Constant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MyClothesServiceImplTest {

    private static final String CLOTHE_NOT_FOUND = "Clothe not found with id = ";
    private static final String USERNAME = "testUsername";
    private static final String SAVED_IMAGE_NAME = "image1.jpg";

    @Mock
    private ClotheRepository clotheRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private MyClothesServiceimpl myClothesService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    private User user;
    private Clothe clothe;
    private ClotheDto clotheDto;
    private MultipartFile image;


    @BeforeEach
    void setUp() {
        //mocking security:
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);

        //provided data:
        image = new MockMultipartFile("images", SAVED_IMAGE_NAME,
                                MediaType.MULTIPART_FORM_DATA_VALUE, new byte[0]);

        user = User.builder().id(2L).email("test@email.com").username(USERNAME)
                .name("test").password("1234qwer")
                .roles(Set.of(new Role(1, "ROLE_USER")))
                .build();

        clothe = Clothe.builder()
                .id(2L)
                .name("Newest clothe")
                .description("clothe for testing")
                .size(ClotheSize.R38)
                .images(List.of(SAVED_IMAGE_NAME))
                .price(BigDecimal.valueOf(132.99))
                .user(user)
                .build();

        clotheDto = ClotheDto.builder()
                .id(2L)
                .name("Newest clothe")
                .description("clothe for testing")
                .size(ClotheSize.R38)
                .images(List.of(SAVED_IMAGE_NAME))
                .price(BigDecimal.valueOf(132.99))
                .userId(user.getId())
                .build();

        when(userRepository.findByUsernameOrEmail(USERNAME, USERNAME)).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        //security closing:
        if (mockedSecurityContextHolder != null) {
            mockedSecurityContextHolder.close();
        }
    }

    @Test
    void givenClotheDto_whenCreateClothe_thenClotheIsSaved() {
        when(mapper.map(clotheDto, Clothe.class)).thenReturn(clothe);
        when(imageService.saveImage(image)).thenReturn(SAVED_IMAGE_NAME);
        when(clotheRepository.save(clothe)).thenReturn(clothe);
        when(mapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        ClotheDto createdClothe = myClothesService.createClothe(clotheDto, List.of(image));

        assertNotNull(createdClothe);
        verify(mapper, times(1)).map(clotheDto, Clothe.class);
        verify(imageService, times(1)).saveImage(image);
        verify(clotheRepository, times(1)).save(clothe);
        verify(mapper, times(1)).map(clothe, ClotheDto.class);
    }

    @Test
    void givenClotheId_whenGetClotheById_thenClotheIsRetrived(){
        when(clotheRepository.existsById(clotheDto.getId())).thenReturn(true);
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of(clothe));
        when(mapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var foundedClothe = myClothesService.getClotheById(clothe.getId());

        assertNotNull(foundedClothe);
        assertEquals(foundedClothe, clotheDto);
        verify(clotheRepository, times(1)).existsById(clotheDto.getId());
        verify(clotheRepository, times(1)).findByUserId(user.getId());
        verify(mapper, times(1)).map(clothe, ClotheDto.class);
    }

    @Test
    void gicenInvalidClotheId_whenGetClotheById_thenResourceNotFoundExceptionIsThrown(){
        when(clotheRepository.existsById(0L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> myClothesService.getClotheById(0L));

        assertEquals(exception.getMessage(), CLOTHE_NOT_FOUND + 0);
        verify(clotheRepository, times(1)).existsById(0L);
        verify(clotheRepository, never()).findByUserId(anyLong());
        verify(mapper, never()).map(clothe, ClotheDto.class);
    }

    @Test
    void gicenUserIsNotTheOwner_whenGetClotheById_thenApiExceptionIsThrown(){
        //clothe belong to user with different ID (different that the logged-in one)
        clotheDto.setUserId(3L);

        when(clotheRepository.existsById(clotheDto.getId())).thenReturn(true);
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of());

        //method will throw an exception, because clothe with this ID isn't the user property
        var apiException = assertThrows(ApiException.class,
                () -> myClothesService.getClotheById(clotheDto.getId()));

        assertEquals(apiException.getMessage(), Constant.NOT_OWNER);
        verify(clotheRepository, times(1)).existsById(clotheDto.getId());
        verify(clotheRepository, times(1)).findByUserId(user.getId());
        verify(mapper, never()).map(clothe, ClotheDto.class);
    }

    @Test
    void whenGetClothes_thenClotheResponseIsRetrived(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Clothe> page = new PageImpl<>(List.of(clothe), pageable, 1);

        when(clotheRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
        when(mapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        ClotheResponse clothes = myClothesService.getClothes(0, 10, "name", "asc");

        assertNotNull(clothes);
        assertTrue(clothes.getClothes().contains(clotheDto));
        verify(clotheRepository, times(1)).findByUserId(user.getId(), pageable);
        verify(mapper, times(1)).map(clothe, ClotheDto.class);
    }

    @Test
    void givenUserDoesNotHaveCLothes_whenGetClothes_thenClotheResponseIsRetrived(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        when(clotheRepository.findByUserId(user.getId(), pageable)).thenReturn(Page.empty());

        var clothes = myClothesService.getClothes(0, 10, "name", "asc");

        assertNotNull(clothes);
        assertTrue(clothes.getClothes().isEmpty());
        verify(clotheRepository, times(1)).findByUserId(user.getId(), pageable);
        verify(mapper, never()).map(clothe, ClotheDto.class);
    }

    //TODO: create test's for update & delete methods
}
