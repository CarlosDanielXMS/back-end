package com.example.back_end.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciais para autenticação")
public record LoginRequest(
        @Schema(description = "E-mail do usuário", example = "admin@teste.com")
        @Email @NotBlank String email,

        @Schema(description = "Senha do usuário", example = "123456")
        @NotBlank String senha
) { }
