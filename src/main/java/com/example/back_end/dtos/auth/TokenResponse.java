package com.example.back_end.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenResponse", description = "Token JWT retornado após autenticação")
public record TokenResponse(
        @Schema(description = "Token JWT no padrão Bearer", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) { }
