package com.example.back_end.dtos.locacao;

import java.math.BigDecimal;

import com.example.back_end.enums.TiposLocacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "LocacaoPatchDTO", description = "Campos parciais para atualização de locação")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocacaoPatchDTO {

    @Schema(description = "Nome da locação", example = "Locação 1")
    @Size(max = 50, message = "O nome não pode exceder {max} caracteres.")
    private String nome;

    @Schema(description = "Tipo da locação", implementation = TiposLocacao.class, example = "RESIDENCIAL")
    @Enumerated(EnumType.STRING)
    private TiposLocacao tipo;

    @Schema(description = "Descrição resumida", example = "Locação 1 fica localizada na...")
    @Size(max = 255, message = "A descrição não pode exceder {max} caracteres.")
    private String descricao;

    @Schema(description = "Valor por hora", example = "120.00")
    @Positive(message = "O valor por hora deve ser um número > 0.")
    private BigDecimal valorHora;

    @Schema(description = "Tempo mínimo (horas)", example = "24")
    @Positive(message = "O tempo mínimo deve ser um número > 0.")
    private Integer tempoMinimo;

    @Schema(description = "Tempo máximo (horas)", example = "72")
    @Positive(message = "O tempo máximo deve ser um número > 0.")
    private Integer tempoMaximo;

    @AssertTrue(message = "O tempo máximo da locação não pode ser menor do que o tempo mínimo.")
    public boolean isTempoRangeValido() {
        if (tempoMinimo == null || tempoMaximo == null) return true;
        return tempoMaximo >= tempoMinimo;
    }
}
