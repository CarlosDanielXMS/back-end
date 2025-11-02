package com.example.back_end.controllers;

import com.example.back_end.auth.JwtService;
import com.example.back_end.dtos.auth.LoginRequest;
import com.example.back_end.dtos.auth.TokenResponse;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock private AuthenticationManager authManager;
    @Mock private JwtService jwt;

    @InjectMocks private AuthController controller;

    @Test
    void login_deveRetornarToken() {
        MockitoAnnotations.openMocks(this);
        LoginRequest req = new LoginRequest("admin@teste.com", "123456");
        Authentication auth = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(auth);
        when(auth.getName()).thenReturn("admin@teste.com");
        when(jwt.generate("admin@teste.com")).thenReturn("header.payload.signature");

        ResponseEntity<TokenResponse> resp = controller.login(req);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().token()).isEqualTo("header.payload.signature");
        verify(authManager).authenticate(any());
        verify(jwt).generate("admin@teste.com");
    }

    @Test
    void login_credencialInvalida_devePropagarExcecao() {
        MockitoAnnotations.openMocks(this);
        LoginRequest req = new LoginRequest("admin@teste.com", "-----");
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> controller.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
