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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class MyClothesServiceimplTest {
    private static final String CLOTHE_NOT_FOUND = "Clothe not found with id = ";
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
        verify(modelMapper, times(1)).map(clothe, ClotheDto.class);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheId_whenGetClotheById_thenClotheIsRetrived(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                        .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of(clothe));
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var foundedClothe = clothesServiceimpl.getClotheById(clothe.getId());

        assertNotNull(foundedClothe);
        assertEquals(foundedClothe, clotheDto);
        verifygetClothe(times(1),
                                times(1), times(1));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void gicenInvalidClotheId_whenGetClotheById_thenApiExceptionIsThrown(){
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of(clothe));

        ApiException apiException = assertThrows(ApiException.class,
                () -> clothesServiceimpl.getClotheById(0L));

        assertApiException(apiException);
        verifygetClothe(times(1), times(1), never());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenUserIsNotTheOwner_whenGetClotheById_thenApiExceptionIsThrown(){
        User otherUser = User.builder().id(3L).email("test@email.com").username("otherUsername")
                .name("test").password("1234qwer").roles(Set.of(new Role(1, "ROLE_USER")))
                .build();
        Clothe otherClothe = Clothe.builder().id(3L).name("Newest clothe").description("clothe for testing")
                .size(ClotheSize.R38).images(List.of("image1.jpg", "image2.png"))
                .price(BigDecimal.valueOf(132.99)).user(user).build();
        clothe.setUser(otherUser);

        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId())).thenReturn(List.of(otherClothe));

        //method will throw an exception, because clothe with this ID isn't the user property
        var apiException = assertThrows(ApiException.class,
                () -> clothesServiceimpl.getClotheById(clothe.getId()));

        assertApiException(apiException);
        verifygetClothe(times(1), times(1), never());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void whenGetClothes_thenClotheResponseIsRetrived(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Clothe> page = new PageImpl<>(List.of(clothe), pageable, 1);

        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        ClotheResponse clothes = clothesServiceimpl.getClothes(0, 10, "name", "asc");

        assertNotNull(clothes);
        assertTrue(clothes.getClothes().contains(clotheDto));
        verifygetClothe(pageable, times(1),
                                  times(1), times(1));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenUserDoesNotHaveCLothes_whenGetClothes_thenClotheResponseIsRetrived(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));
        when(clotheRepository.findByUserId(user.getId(), pageable)).thenReturn(Page.empty());

        var clothes = clothesServiceimpl.getClothes(0, 10, "name", "asc");

        assertNotNull(clothes);
        assertTrue(clothes.getClothes().isEmpty());
        verifygetClothe(pageable,
            times(1), times(1), never());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheDtoAndId_whenUpdateClothe_thenClotheIsUpdated(){
        clotheDto.setPrice(clotheDto.getPrice().add(BigDecimal.valueOf(15)));
        clotheDto.setName(clotheDto.getName() + " updated!");

        when(clotheRepository.findById(clothe.getId())).thenReturn(Optional.of(clothe));
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername())).thenReturn(Optional.of(user));
        when(clotheRepository.save(clothe)).thenReturn(clothe);
        when(modelMapper.map(clothe, ClotheDto.class)).thenReturn(clotheDto);

        var updatedClotheDto = clothesServiceimpl.updateClothe(clothe.getId(), clotheDto);

        assertNotNull(updatedClotheDto);
        assertEquals(updatedClotheDto.getName(), clothe.getName());
        assertEquals(updatedClotheDto.getPrice(), clothe.getPrice());
        verifyOperation(clothe.getId(), times(1), times(1),
                                        repo -> verify(repo, times(1)).save(clothe));
        verify(modelMapper, times(1)).map(clothe, ClotheDto.class);
    }


    @Test
    void givenInvalidClotheId_whenUpdateClothe_thenResourceNotFoundExceptionItThrown(){
        clotheDto.setPrice(clotheDto.getPrice().add(BigDecimal.valueOf(15)));
        clotheDto.setName(clotheDto.getName() + " updated!");
        clothe.setId(0L);

        when(clotheRepository.findById(clothe.getId())).thenThrow(
                new ResourceNotFoundException("Clothe", "id", clothe.getId()));

        var exception = assertThrows(ResourceNotFoundException.class,
                                            () -> clothesServiceimpl.updateClothe(clothe.getId(), clotheDto));

        assertEquals(exception.getMessage(), CLOTHE_NOT_FOUND + clothe.getId());
        verifyOperation(clothe.getId(), times(1), never(),
                                                    repo -> verify(repo, never()).save(clothe));
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenUserIsNotheOwner_whenUpdateClothe_thenApiExceptionIsThrown(){
        clotheDto.setPrice(clotheDto.getPrice().add(BigDecimal.valueOf(15)));
        clotheDto.setName(clotheDto.getName() + " updated!");
        User otherUser = User.builder().id(2L).email("otherUser@email.com").username("other username")
                        .name("otherUser").password("1234qwer")
                        .roles(Set.of(new Role(1, "ROLE_USER"))).build();
        //clothe is owned by another user
        clothe.setUser(otherUser);
        when(clotheRepository.findById(clothe.getId())).thenReturn(Optional.of(clothe));
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                                    .thenReturn(Optional.of(user));

        var exception = assertThrows(ApiException.class,
                                () -> clothesServiceimpl.updateClothe(clothe.getId(), clotheDto));

        assertApiException(exception);
        assertNotEquals(clothe.getPrice(), clotheDto.getPrice());
        assertNotEquals(clothe.getName(), clotheDto.getName());
        verifyOperation(clothe.getId(), times(1), times(1),
                                                 repo -> verify(repo, never()).save(clothe));
        verify(modelMapper, never()).map(clothe, ClotheDto.class);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenClotheId_whenDeleteClothe_thenClotheIsDeleted(){
        when(clotheRepository.findById(clothe.getId())).thenReturn(Optional.of(clothe));
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                                                                .thenReturn(Optional.of(user));

        String message = clothesServiceimpl.deleteClothe(clothe.getId());

        assertEquals(message, "Clothe deleted successfully!");
        verifyOperation(clothe.getId(), times(1), times(1),
                        repo -> verify(repo, times(1)).delete(clothe));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenInvalidClotheId_whenDeleteClothe_thenResourceNotFoundExceptionIsThrown(){
        clothe.setId(0L);
        when(clotheRepository.findById(clothe.getId())).thenThrow(
                new ResourceNotFoundException("Clothe", "id", clothe.getId()));

        var excpetion = assertThrows(ResourceNotFoundException.class,
                                () -> clothesServiceimpl.deleteClothe(clothe.getId()));

        assertEquals(excpetion.getMessage(), CLOTHE_NOT_FOUND + clothe.getId());
        verifyOperation(clothe.getId(), times(1), never(),
                repo -> verify(repo, never()).delete(clothe));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void givenUserIsNotTheOwner_whenDeleteClothe_thenApiExceptionIsThrown(){
        User otherUser = User.builder().id(2L).email("otherUser@email.com")
                .username("other username").name("otherUser").password("1234qwer")
                .roles(Set.of(new Role(1, "ROLE_USER"))).build();
        clothe.setUser(otherUser);

        when(clotheRepository.findById(clothe.getId())).thenReturn(Optional.of(clothe));
        when(userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername()))
                .thenReturn(Optional.of(user));

        var exception = assertThrows(ApiException.class,
                                () -> clothesServiceimpl.deleteClothe(clothe.getId()));

        assertApiException(exception);
        verifyOperation(clothe.getId(), times(1), times(1),
                repo -> verify(repo, never()).delete(clothe));
    }



    private static void assertApiException(ApiException apiException){
        assertEquals(apiException.getStatus(), HttpStatus.FORBIDDEN);
        assertEquals(apiException.getMessage(), MyClothesServiceimpl.NOT_OWNER);
    }
    private void verifygetClothe(Pageable pageable, VerificationMode verifyUser,
                                 VerificationMode verifyClothe, VerificationMode verifyMap){
        verify(userRepository, verifyUser).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(clotheRepository, verifyClothe).findByUserId(user.getId(), pageable);
        verify(modelMapper, verifyMap).map(clothe, ClotheDto.class);
    }
    private void verifygetClothe(VerificationMode verifyUser,
                                 VerificationMode verifyClothe, VerificationMode verifyMap){
        verify(userRepository, verifyUser).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        verify(clotheRepository, verifyClothe).findByUserId(user.getId());
        verify(modelMapper, verifyMap).map(clothe, ClotheDto.class);
    }
    private void verifyOperation(long clotheId, VerificationMode verifyFind,
                                            VerificationMode verifyUser, Consumer<ClotheRepository> action){
        verify(clotheRepository, verifyFind).findById(clotheId);
        verify(userRepository, verifyUser).findByUsernameOrEmail(user.getUsername(), user.getUsername());
        action.accept(clotheRepository);
    }
}
