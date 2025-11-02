package com.example.back_end.dtos.cliente;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "ClientePatchDTO", description = "Campos parciais para atualização de cliente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientePatchDTO {

    @Schema(description = "Nome do cliente", example = "Nome 1")
    @Size(max = 50, message = "O nome não pode exceder {max} caracteres.")
    private String nome;

    @Schema(description = "Telefone com DDI/DDD", example = "+5517996710451")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Telefone deve conter de 10 a 15 dígitos (com ou sem +).")
    private String telefone;

    @Schema(description = "CPF apenas dígitos", example = "12345678901")
    @Size(min = 11, max = 11, message = "O CPF deve conter exatamente 11 dígitos.")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter apenas dígitos (sem pontos ou hífen).")
    @CPF(message = "CPF inválido.")
    private String cpf;
}
