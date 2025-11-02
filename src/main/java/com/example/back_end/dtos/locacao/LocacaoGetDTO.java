package com.example.back_end.dtos.locacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.back_end.enums.TiposLocacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "LocacaoGetDTO", description = "Representação de locação para leitura")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocacaoGetDTO {

    @Schema(description = "ID da locação", example = "1")
    private Integer id;

    @Schema(description = "Nome da locação", example = "Locação 1")
    private String nome;

    @Schema(description = "Tipo da locação", implementation = TiposLocacao.class, example = "RESIDENCIAL")
    private TiposLocacao tipo;

    @Schema(description = "Descrição resumida", example = "Locação 1 fica localizada na...")
    private String descricao;

    @Schema(description = "Valor por hora", example = "120.00")
    private BigDecimal valorHora;

    @Schema(description = "Tempo mínimo (horas)", example = "24")
    private Integer tempoMinimo;

    @Schema(description = "Tempo máximo (horas)", example = "72")
    private Integer tempoMaximo;

    @Schema(description = "Data/hora de criação (ISO-8601)", type = "string", format = "date-time",
            example = "2025-10-31T14:23:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
}
