package com.example.back_end.controllers;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.configs.OpenApiConfig;
import com.example.back_end.dtos.cliente.*;
import com.example.back_end.services.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@SecurityRequirement(name = OpenApiConfig.BEARER_KEY)
@RequiredArgsConstructor
@Validated
@Tag(name = "Clientes")
public class ClienteController {

    private final ClienteService service;

    @GetMapping
    @Operation(summary = "Lista clientes com paginação")
    @Parameters({
        @Parameter(name = "page", description = "Página (0-base)", example = "0"),
        @Parameter(name = "size", description = "Tamanho da página", example = "20"),
        @Parameter(name = "sort", description = "Ordenação: campo,dir (asc|desc). Pode repetir.", example = "id,asc")
    })
    public ResponseEntity<Page<ClienteGetDTO>> listarTodos(
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca cliente por ID")
    public ResponseEntity<ClienteGetDTO> buscarPorId(
            @Parameter(description = "Identificador do cliente", example = "1")
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cria um novo cliente")
    @ApiResponse(responseCode = "201", description = "Criado (Location no header)")
    public ResponseEntity<ClienteGetDTO> criar(
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Dados do cliente a ser criado", required = true)
            ClientePostDTO dto) {
        ClienteGetDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/clientes/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza cliente (PUT)")
    public ResponseEntity<ClienteGetDTO> atualizar(
            @Parameter(description = "Identificador do cliente", example = "1")
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Dados completos para atualização", required = true)
            ClientePutDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente um cliente (PATCH)")
    public ResponseEntity<ClienteGetDTO> atualizarParcial(
            @Parameter(description = "Identificador do cliente", example = "1")
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Campos parciais para atualização", required = true)
            ClientePatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove cliente por ID")
    @ApiResponse(responseCode = "204", description = "Removido (sem corpo)")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador do cliente", example = "1")
            @PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
