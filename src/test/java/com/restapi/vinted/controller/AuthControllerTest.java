package com.restapi.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.JwtAuthResponse;
import com.restapi.vinted.payload.LoginDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String BASE_URL = "/api/auth";
    private static final String USERNAME = "username";

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
    void givenNull_whenLogin_thenRequestFailed() throws Exception{
        ResultActions response = mockMvc.perform(post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Failed to read request")));
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
                .andExpect(jsonPath("$.message", is(exception.getMessage())))
                .andDo(print());
        verify(authService, times(1)).login(any(LoginDto.class));
    }

    @Test
    void register(){
    }
}