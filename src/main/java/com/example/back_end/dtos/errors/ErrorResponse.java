package com.example.back_end.dtos.errors;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse",
        description = "Estrutura padrão para respostas de erro da API")
public record ErrorResponse(
        @Schema(description = "Momento do erro (UTC)", type = "string", format = "date-time",
                example = "2025-10-31T14:23:11Z")
        OffsetDateTime timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Resumo do erro", example = "Validation failed")
        String message,

        @ArraySchema(arraySchema = @Schema(description = "Violações de validação"),
                schema = @Schema(implementation = Violation.class))
        List<Violation> errors,

        @Schema(description = "Caminho do recurso", example = "/reservas")
        String path) {

    public static ErrorResponse simple(HttpStatus status, String message, String path) {
        return new ErrorResponse(OffsetDateTime.now(), status.value(), message, List.of(), path);
    }
}
