package com.restapi.vinted.service;


import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class MyClothesServiceImplTest {

    @Mock
    private ClotheRepository clotheRepository;

    @Mock
    private ImageService imageService;

    @Mock(lenient = true)       //FIXME: nie może tak zostać!
    private ModelMapper mapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyClothesServiceimpl myClothesService;

    private User user;

    private Clothe clothe;
    private Clothe savedClothe;

    private ClotheDto clotheDto;
    private ClotheDto savedClotheDto;

    List<MultipartFile> images;
    List<String> imagesString;

    private static final String USERNAME = "testUsername";

    @BeforeEach
    void setUp() {
        images = List.of(new MockMultipartFile("images", "image1.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE, new byte[0]));
        imagesString = images.stream().map(MultipartFile::getOriginalFilename).toList();

        user = User.builder().id(2L).email("test@email.com").username(USERNAME)
                .name("test").password("1234qwer")
                .roles(Set.of(new Role(1, "ROLE_USER")))
                .build();

        clothe = Clothe.builder()
                .name("Newest clothe")
                .description("clothe for testing")
                .size(ClotheSize.R38)
                .price(BigDecimal.valueOf(132.99))
                .user(user)
                .images(imagesString) // Dodano obrazy
                .build();

        savedClothe = Clothe.builder()
                .id(2L)
                .name("Newest clothe")
                .description("clothe for testing")
                .size(ClotheSize.R38)
                .images(imagesString)
                .price(BigDecimal.valueOf(132.99))
                .user(user)
                .build();

        clotheDto = ClotheDto.builder()
                .name("Newest clothe")
                .description("clothe for testing")
                .size(ClotheSize.R38)
                .price(BigDecimal.valueOf(132.99))
                .build();

        savedClotheDto = ClotheDto.builder()
                .id(2L)
                .name("Newest clothe")
                .description("clothe for testing")
                .size(ClotheSize.R38)
                .images(imagesString)
                .price(BigDecimal.valueOf(132.99))
                .userId(user.getId())
                .build();

        when(userRepository.findByUsernameOrEmail(USERNAME, USERNAME)).thenReturn(Optional.of(user));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void testCreateClothe() {
        when(mapper.map(any(ClotheDto.class), eq(Clothe.class))).thenReturn(clothe);
        when(imageService.saveImage(any())).thenReturn("image.jpg");
        when(clotheRepository.save(any(Clothe.class))).thenReturn(savedClothe);
        when(mapper.map(savedClothe, ClotheDto.class)).thenReturn(savedClotheDto);

        ClotheDto createdClothe = myClothesService.createClothe(clotheDto, images);

        assertEquals("Newest clothe", createdClothe.getName());
        verify(clotheRepository, times(1)).save(any(Clothe.class));
    }



    @Test
    void testGetClotheById_WhenNotExists() {
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of());

        ApiException exception = assertThrows(ApiException.class, () ->
            myClothesService.getClotheById(1L));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void testGetClothes() {
        Clothe clothe = new Clothe();
        clothe.setId(1L);
        clothe.setName("Shirt");
        Page<Clothe> page = new PageImpl<>(List.of(clothe));

        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(eq(user.getId()), any(Pageable.class))).thenReturn(page);
        when(mapper.map(any(Clothe.class), eq(ClotheDto.class))).thenReturn(new ClotheDto());

        ClotheResponse response = myClothesService.getClothes(0, 5, "name", "asc");

        assertNotNull(response);
        assertEquals(1, response.getClothes().size());
        verify(clotheRepository, times(1)).findByUserId(eq(user.getId()), any(Pageable.class));
    }

    @Test
    void testUpdateClothe() {
        Clothe clothe = new Clothe();
        clothe.setId(1L);
        clothe.setUser(user);
        ClotheDto clotheDto = new ClotheDto();
        clotheDto.setName("New Shirt");
        clotheDto.setPrice(new BigDecimal("35.50"));

        when(clotheRepository.findById(anyLong())).thenReturn(Optional.of(clothe));
        when(clotheRepository.save(any(Clothe.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.map(any(Clothe.class), eq(ClotheDto.class))).thenReturn(clotheDto);

        ClotheDto updatedClothe = myClothesService.updateClothe(1L, clotheDto, null, null);

        assertEquals("New Shirt", updatedClothe.getName());
        verify(clotheRepository, times(1)).save(clothe);
    }

    @Test
    void testDeleteClothe() {
        Clothe clothe = new Clothe();
        clothe.setId(1L);
        clothe.setUser(user);

        when(clotheRepository.findById(anyLong())).thenReturn(Optional.of(clothe));
        doNothing().when(clotheRepository).delete(any(Clothe.class));

        String result = myClothesService.deleteClothe(1L);

        assertEquals("Clothe deleted successfully!", result);
        verify(clotheRepository, times(1)).delete(clothe);
    }

    @Test
    void testDeleteClothe_WhenNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        Clothe clothe = new Clothe();
        clothe.setId(1L);
        clothe.setUser(anotherUser);

        when(clotheRepository.findById(anyLong())).thenReturn(Optional.of(clothe));

        ApiException exception = assertThrows(ApiException.class, () -> {
            myClothesService.deleteClothe(1L);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        verify(clotheRepository, times(0)).delete(clothe);
    }
}
