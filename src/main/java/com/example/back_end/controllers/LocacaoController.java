package com.example.back_end.controllers;

import java.net.URI;
import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.configs.OpenApiConfig;
import com.example.back_end.dtos.locacao.*;
import com.example.back_end.services.LocacaoService;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/locacoes")
@SecurityRequirement(name = OpenApiConfig.BEARER_KEY)
@RequiredArgsConstructor
@Validated
@Tag(name = "Locações")
public class LocacaoController {

    private final LocacaoService service;

    @GetMapping
    @Operation(summary = "Lista locações com paginação")
    @Parameters({
        @Parameter(name = "page", description = "Página (0-base)", example = "0"),
        @Parameter(name = "size", description = "Tamanho da página", example = "20"),
        @Parameter(name = "sort", description = "Ordenação: campo,dir (asc|desc). Pode repetir.", example = "id,asc")
    })
    public ResponseEntity<Page<LocacaoGetDTO>> listarTodos(
            @ParameterObject
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca locação por ID")
    public ResponseEntity<LocacaoGetDTO> buscarPorId(
            @Parameter(description = "Identificador da locação", example = "")
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Lista locações disponíveis em uma data")
    @Parameters({
        @Parameter(name = "page", description = "Página (0-base)", example = "0"),
        @Parameter(name = "size", description = "Tamanho da página", example = "20"),
        @Parameter(name = "sort", description = "Ordenação: campo,dir (asc|desc). Pode repetir.", example = "id,asc")
    })
    public Page<LocacaoGetDTO> disponiveisPorData(
            @RequestParam
            @DateTimeFormat(iso = ISO.DATE)
            @Parameter(description = "Data de referência", example = "2025-10-30",
                       schema = @Schema(type = "string", format = "date"))
            LocalDate data,
            @ParameterObject Pageable pageable) {
        return service.listarDisponiveis(data, pageable);
    }

    @GetMapping("/disponiveis-entre")
    @Operation(summary = "Lista locações disponíveis entre duas datas")
    @Parameters({
        @Parameter(name = "page", description = "Página (0-base)", example = "0"),
        @Parameter(name = "size", description = "Tamanho da página", example = "20"),
        @Parameter(name = "sort", description = "Ordenação: campo,dir (asc|desc). Pode repetir.", example = "id,asc")
    })
    public Page<LocacaoGetDTO> disponiveisEntre(
            @RequestParam
            @DateTimeFormat(iso = ISO.DATE)
            @Parameter(description = "Data inicial", example = "2025-10-30",
                       schema = @Schema(type = "string", format = "date"))
            LocalDate inicio,
            @RequestParam
            @DateTimeFormat(iso = ISO.DATE)
            @Parameter(description = "Data final", example = "2025-10-31",
                       schema = @Schema(type = "string", format = "date"))
            LocalDate fim,
            @ParameterObject Pageable pageable) {
        return service.listarDisponiveis(inicio, fim, pageable);
    }

    @PostMapping
    @Operation(summary = "Cria uma nova locação")
    @ApiResponse(responseCode = "201", description = "Criado (Location no header)")
    public ResponseEntity<LocacaoGetDTO> criar(
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Dados da locação a ser criada", required = true)
            LocacaoPostDTO dto) {
        LocacaoGetDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/locacoes/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza locação (PUT)")
    public ResponseEntity<LocacaoGetDTO> atualizar(
            @Parameter(description = "Identificador da locação", example = "1")
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Dados completos para atualização", required = true)
            LocacaoPutDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente uma locação (PATCH)")
    public ResponseEntity<LocacaoGetDTO> atualizarParcial(
            @Parameter(description = "Identificador da locação", example = "1")
            @PathVariable Integer id,
            @org.springframework.web.bind.annotation.RequestBody @Valid
            @RequestBody(description = "Campos parciais para atualização", required = true)
            LocacaoPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove locação por ID")
    @ApiResponse(responseCode = "204", description = "Removido (sem corpo)")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador da locação", example = "1")
            @PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
