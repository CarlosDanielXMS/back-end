package com.example.back_end.dtos.cliente;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "ClienteGetDTO", description = "Representação de cliente para leitura")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteGetDTO {

    @Schema(description = "ID do cliente", example = "1")
    private Integer id;

    @Schema(description = "Nome do cliente", example = "Nome 1")
    private String nome;

    @Schema(description = "E-mail do cliente", example = "email1@exemplo.com")
    private String email;

    @Schema(description = "Telefone com DDI/DDD", example = "+5517996710451")
    private String telefone;

    @Schema(description = "CPF apenas dígitos", example = "12345678901")
    private String cpf;

    @Schema(description = "Data/hora de criação do registro (ISO-8601)",
            type = "string", format = "date-time", example = "2025-10-31T14:23:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
}
