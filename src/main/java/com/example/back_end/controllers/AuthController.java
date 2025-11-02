package com.example.back_end.controllers;

import com.example.back_end.dtos.auth.LoginRequest;
import com.example.back_end.dtos.auth.TokenResponse;
import com.example.back_end.auth.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Autenticação")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwt;

  @PostMapping("/login")
  @Operation(
      summary = "Autentica usuário e retorna o token JWT",
      description = "Endpoint público para login.",
      security = {}
  )
  @ApiResponse(responseCode = "200", description = "Login bem-sucedido")
  @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
  public ResponseEntity<TokenResponse> login(
      @org.springframework.web.bind.annotation.RequestBody @Valid
      @RequestBody(
          description = "Credenciais do usuário",
          required = true,
          content = @Content(examples = @ExampleObject(
              name = "Exemplo de login",
              value = """
                      { "email": "admin@teste.com", "senha": "123456" }
                      """
          ))
      )
      LoginRequest req) {

    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.email(), req.senha()));
    String token = jwt.generate(auth.getName());
    return ResponseEntity.ok(new TokenResponse(token));
  }
}
