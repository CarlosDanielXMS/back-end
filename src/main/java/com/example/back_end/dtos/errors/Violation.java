package com.example.back_end.dtos.errors;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Violation", description = "Detalhe de uma violação de validação")
public record Violation(
        @Schema(description = "Campo inválido", example = "dataFim")
        String field,

        @Schema(description = "Mensagem de validação", example = "deve ser uma data posterior a dataInicio")
        String message,

        @Schema(description = "Valor rejeitado")
        Object rejectedValue
) { }
