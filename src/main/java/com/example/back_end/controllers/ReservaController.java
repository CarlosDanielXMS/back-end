package com.example.back_end.controllers;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.configs.OpenApiConfig;
import com.example.back_end.dtos.reserva.*;
import com.example.back_end.services.ReservaService;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservas")
@SecurityRequirement(name = OpenApiConfig.BEARER_KEY)
@RequiredArgsConstructor
@Validated
@Tag(name = "Reservas")
public class ReservaController {

    private final ReservaService service;

    @GetMapping
    @Operation(summary = "Lista reservas com paginação")
    @Parameters({
        @Parameter(name = "page", description = "Página (0-base)", example = "0"),
        @Parameter(name = "size", description = "Tamanho da página", example = "20"),
        @Parameter(name = "sort", description = "Ordenação: campo,dir (asc|desc). Pode repetir.", example = "id,asc")
    })
    public ResponseEntity<Page<ReservaGetDTO>> listarTodos(
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca reserva por ID")
    public ResponseEntity<ReservaGetDTO> buscarPorId(
            @Parameter(description = "Identificador da reserva", example = "1")
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova reserva")
    @ApiResponse(responseCode = "201", description = "Criado (Location no header)")
    public ResponseEntity<ReservaGetDTO> criar(
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Dados da reserva a ser criada", required = true)
            ReservaPostDTO dto) {
        ReservaGetDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/reservas/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza reserva (PUT)")
    public ResponseEntity<ReservaGetDTO> atualizar(
            @Parameter(description = "Identificador da reserva", example = "1")
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Dados completos para atualização", required = true)
            ReservaPutDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente uma reserva (PATCH)")
    public ResponseEntity<ReservaGetDTO> atualizarParcial(
            @Parameter(description = "Identificador da reserva", example = "1")
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Campos parciais para atualização", required = true)
            ReservaPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove reserva por ID")
    @ApiResponse(responseCode = "204", description = "Removido (sem corpo)")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador da reserva", example = "1")
            @PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
