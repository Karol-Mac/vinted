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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
                        loginDto.getUsernameOrEmail(), loginDto.getPassword())))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);

        JwtAuthResponse response = authServiceImpl.login(loginDto);

        assertEquals(token, response.getAccessToken());
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken(
                                    loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        verify(jwtTokenProvider, times(1))
                .generateToken(authentication);
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenUserIsLoggedIn() {
        LoginDto loginDto = new LoginDto("username", "wrong password");

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()))).thenThrow(BadCredentialsException.class);

        // Test & Verify
        ApiException exception = assertThrows(ApiException.class, () -> authServiceImpl.login(loginDto));

        assertEquals(exception.getStatus(), HttpStatus.UNAUTHORIZED);
        assertEquals(exception.getMessage(), "Wrong username/email or password");
        verify(authenticationManager, times(1)).authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        verify(jwtTokenProvider, never()).generateToken(any());
    }


    @Test
    void givenRegisterDto_whenRegister_then() {
        RegisterDto registerDto = new RegisterDto("name", "username",
                                                  "test@email.com", "password");
        Role userRole = new Role(1L,"ROLE_USER");
        User user = User.builder().id(2L).email(registerDto.getEmail())
                        .name(registerDto.getName()).password(registerDto.getPassword())
                        .username(registerDto.getUsername())
                        .roles(Set.of(new Role(1, "ROLE_USER"))).build();
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(roleRepository.findByName(userRole.getName())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        String response = authServiceImpl.register(registerDto);

        assertEquals("User sign up successfully", response);
        verify(userRepository, times(1))
                                            .existsByUsername(registerDto.getUsername());
        verify(userRepository, times(1))
                                            .existsByEmail(registerDto.getEmail());
        verify(roleRepository, times(1)).findByName(userRole.getName());
        verify(passwordEncoder, times(1)).encode(registerDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUsernameExist_whenRegister_thenApiExceptionIsThrown() {
        RegisterDto registerDto = new RegisterDto("name", "existingUsername",
                                                  "test@email.com", "password");
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(true);

        var exception = assertThrows(ApiException.class, () -> authServiceImpl.register(registerDto));
        assertEquals(exception.getMessage(), "Username is already taken");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());

        verify(userRepository, never()).existsByEmail(anyString());
        verify(roleRepository, never()).findByName(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenEmailExist_whenRegister_thenApiExceptionIsThrown() {
        RegisterDto registerDto = new RegisterDto("name", "username",
                                                  "existingTest@email.com", "password");
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        // Test & Verify
        var exception = assertThrows(ApiException.class, () -> authServiceImpl.register(registerDto));
        assertEquals(exception.getMessage(), "Email already exist");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());

        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
        verify(roleRepository, never()).findByName(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}