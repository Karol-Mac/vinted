package com.restapi.vinted.service;

import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.JwtAuthResponse;
import com.restapi.vinted.payload.LoginDto;
import com.restapi.vinted.payload.RegisterDto;
import com.restapi.vinted.repository.RoleRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.security.JwtTokenProvider;
import com.restapi.vinted.service.impl.AuthServiceimpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceimplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private AuthServiceimpl authServiceImpl;

    @Test
    void givenLoginDto_whenLogin_thenUserIsLoggedIn() {
        LoginDto loginDto = new LoginDto("username", "password");
        Authentication authentication = Mockito.mock(Authentication.class);
        String token = "testAccesToken";

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(), loginDto.getPassword()))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);

        JwtAuthResponse response = authServiceImpl.login(loginDto);

        assertEquals(token, response.getAccessToken());
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenApiExceptionIsThrown() {
        LoginDto loginDto = new LoginDto("username", "wrong password");

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()))).thenThrow(BadCredentialsException.class);

        // Test & Verify
        ApiException exception = assertThrows(ApiException.class, () -> authServiceImpl.login(loginDto));

        assertApiException(exception, "Wrong username/email or password", HttpStatus.UNAUTHORIZED);
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        verify(jwtTokenProvider, never()).generateToken(any());
    }


    @Test
    void givenRegisterDto_whenRegister_thenRegister() {
        RegisterDto registerDto = getRegisterDto();
        Role userRole = new Role(1L,"ROLE_USER");
        User user = User.builder().id(2L).email(registerDto.getEmail())
                        .name(registerDto.getName()).password(registerDto.getPassword())
                        .username(registerDto.getUsername())
                        .roles(Set.of(new Role(1, "ROLE_USER"))).build();

        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(roleRepository.findByName(userRole.getName())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String response = authServiceImpl.register(registerDto);

        assertEquals("User sign up successfully", response);
        verifyMocksOperations(registerDto, times(1), times(1),
                           times(1), times(1),
                           times(1), userRole.getName());
    }

    @Test
    void givenUsernameExist_whenRegister_thenApiExceptionIsThrown() {
        RegisterDto registerDto = getRegisterDto();
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(true);

        var exception = assertThrows(ApiException.class, () -> authServiceImpl.register(registerDto));

        assertApiException(exception, "Username is already taken", HttpStatus.BAD_REQUEST);
        verifyMocksOperations(registerDto, times(1), never(),
                never(), never(), never(), "");
    }

    @Test
    void givenEmailExist_whenRegister_thenApiExceptionIsThrown() {
        RegisterDto registerDto = getRegisterDto();
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        // Test & Verify
        var exception = assertThrows(ApiException.class, () -> authServiceImpl.register(registerDto));

        assertApiException(exception, "Email already exist", HttpStatus.BAD_REQUEST);


        verifyMocksOperations(registerDto, times(1), times(1),
                 never(), never(), never(), "");
    }

    private static void assertApiException(@NotNull ApiException exception, String message, HttpStatus status){
        assertEquals(exception.getMessage(), message);
        assertEquals(exception.getStatus(), status);
    }
    @NotNull
    private static RegisterDto getRegisterDto(){
        return new RegisterDto("name", "username",
                "test@email.com", "password");
    }
    private void verifyMocksOperations(@NotNull RegisterDto registerDto, VerificationMode verifyExistByUsername,
                                       VerificationMode verifyExistByEmail, VerificationMode findByName,
                                       VerificationMode encode, VerificationMode save, String roleName){
        verify(userRepository, verifyExistByUsername).existsByUsername(registerDto.getUsername());
        verify(userRepository, verifyExistByEmail).existsByEmail(registerDto.getEmail());
        verify(roleRepository, findByName).findByName(roleName);
        verify(passwordEncoder, encode).encode(registerDto.getPassword());
        verify(userRepository, save).save(any(User.class));
    }
}