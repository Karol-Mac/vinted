package com.restapi.vinted.controller;

import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.JwtAuthResponse;
import com.restapi.vinted.payload.LoginDto;
import com.restapi.vinted.payload.RegisterDto;
import com.restapi.vinted.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints related to authentication")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(summary = "Login or Sign in", description = "Authenticate user with username and password to obtain a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, missing or invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(
            @RequestBody(required = false) @Valid LoginDto loginDto){

        if(loginDto == null)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request body is missing");

        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(
                    @RequestBody(required = false) @Valid RegisterDto registerDto){

        if(registerDto == null)
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Request body is missing");

        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
    }
}
