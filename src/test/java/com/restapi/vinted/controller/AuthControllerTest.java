package com.restapi.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.JwtAuthResponse;
import com.restapi.vinted.payload.LoginDto;
import com.restapi.vinted.payload.RegisterDto;
import com.restapi.vinted.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String BASE_URL = "/api/auth";
    private static final String REGISTER_SUCCESFULLY = "User sign up successfully";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private final JwtAuthResponse jwtResponse = new JwtAuthResponse();

    @Test
    void givenLoginDto_whenLogin_thenJwtAuthResponseIsRetrived() throws Exception{
        LoginDto loginDto = new LoginDto("givenUsername", "userPasswd");
        jwtResponse.setAccessToken("testedToken");
        when(authService.login(any(LoginDto.class))).thenReturn(jwtResponse);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/login")
                            .content(objectMapper.writeValueAsString(loginDto))
                            .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is(jwtResponse.getAccessToken())))
                .andExpect(jsonPath("$.tokenType", is(jwtResponse.getTokenType())));
    }

    @Test
    void givenLoginDtoOnSingInPath_whenLogin_thenJwtAuthResponseIsRetrived() throws Exception{
        LoginDto loginDto = new LoginDto("givenUsername", "userPasswd");
        jwtResponse.setAccessToken("testedToken");
        when(authService.login(any(LoginDto.class))).thenReturn(jwtResponse);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/signin")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is(jwtResponse.getAccessToken())))
                .andExpect(jsonPath("$.tokenType", is(jwtResponse.getTokenType())));
    }

    @Test
    void givenNull_whenLogin_thenRequestFailed() throws Exception{
        ResultActions response = mockMvc.perform(post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        is("Request body is missing")));
        verify(authService, never()).login(any());
    }

    @Test
    void givenEmptyLoginDto_whenLogin_thenValidationFailed() throws Exception{
        String message = "must not be null";

        ResultActions response = mockMvc.perform(post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginDto())));

        response.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.usernameOrEmail", is(message)),
                        jsonPath("$.usernameOrEmail", is(message))
                );
        verify(authService, never()).login(any());
    }

    @Test
    void givenBadCredentials_whenLogin_thenApiExceptionIsThrown() throws Exception{
        LoginDto loginDto = new LoginDto("username", "wrongPasswd");

        var exception = new ApiException(HttpStatus.FORBIDDEN,"Wrong username/email or password");
        when(authService.login(any(LoginDto.class))).thenThrow(exception);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        response.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
        verify(authService, times(1)).login(any(LoginDto.class));
    }

    @Test
    void givenRegisterDto_whenRegister_thenNewUserIsCreated() throws Exception{
        RegisterDto registerDto = new RegisterDto("user",
                                    "username", "test@email.com", "1@Qwerty");

        when(authService.register(any(RegisterDto.class))).thenReturn(REGISTER_SUCCESFULLY);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        response.andExpect(status().isCreated())
                .andExpect(content().string(REGISTER_SUCCESFULLY));
    }

    @Test
    void givenRegisterDtoOnSignUpPath_whenRegister_thenNewUserIsCreated() throws Exception{
        RegisterDto registerDto = new RegisterDto("user",
                "username", "test@email.com", "1@Qwerty");

        when(authService.register(any(RegisterDto.class))).thenReturn(REGISTER_SUCCESFULLY);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        response.andExpect(status().isCreated())
                .andExpect(content().string(REGISTER_SUCCESFULLY));
    }

    @Test
    void givenNull_whenRegister_thenRequestFailed() throws Exception{
        ResultActions response = mockMvc.perform(post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Request body is missing")));
        verify(authService, never()).register(any());
    }

    @Test
    void givenEmptyRegisterDto_whenRegister_thenValidationFailed() throws Exception{
        String message = "must not be null";

        ResultActions response = mockMvc.perform(post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterDto())));

        response.andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.password", is(message)),
                        jsonPath("$.name", is("must not be empty")),
                        jsonPath("$.email", is(message)),
                        jsonPath("$.username", is(message))
                );
        verify(authService, never()).login(any());
    }

    @Test
    void givenExistingUsername_whenRegister_thenApiExceptionIsThrown() throws Exception{
        RegisterDto registerDto = new RegisterDto("existingUser",
                "username", "test@email.com", "1@Qwerty");
        var exception = new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken");

        when(authService.register(any(RegisterDto.class))).thenThrow(exception);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }

    @Test
    void givenExistingEmail_whenRegister_thenApiExceptionIsThrown() throws Exception{
        RegisterDto registerDto = new RegisterDto("User",
                "username", "existingEmail@email.com", "1@Qwerty");
        var exception = new ApiException(HttpStatus.BAD_REQUEST, "Email already exist");

        when(authService.register(any(RegisterDto.class))).thenThrow(exception);

        ResultActions response = mockMvc.perform(post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(exception.getMessage())));
    }
}