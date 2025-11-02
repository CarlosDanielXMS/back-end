package com.example.back_end.dtos.locacao;

import java.math.BigDecimal;

import com.example.back_end.enums.TiposLocacao;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "LocacaoPostDTO", description = "Dados para criação de locação")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocacaoPostDTO {

    @Schema(description = "Nome da locação", example = "Locação 1")
    @NotBlank(message = "O nome é um campo obrigatório.")
    @Size(max = 50, message = "O nome não pode exceder {max} caracteres.")
    private String nome;

    @Schema(description = "Tipo da locação", implementation = TiposLocacao.class, example = "RESIDENCIAL")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "O tipo de locação é um campo obrigatório.")
    private TiposLocacao tipo;

    @Schema(description = "Descrição resumida", example = "Locação 1 fica localizada na...")
    @Size(max = 255, message = "A descrição não pode exceder {max} caracteres.")
    private String descricao;

    @Schema(description = "Valor por hora", example = "120.00")
    @NotNull(message = "O valor por hora é um campo obrigatório.")
    @Positive(message = "O valor por hora deve ser um número > 0.")
    private BigDecimal valorHora;

    @Schema(description = "Tempo mínimo (horas)", example = "24")
    @NotNull(message = "O tempo mínimo é um campo obrigatório.")
    @Positive(message = "O tempo mínimo deve ser um número > 0.")
    private Integer tempoMinimo;

    @Schema(description = "Tempo máximo (horas)", example = "72")
    @NotNull(message = "O tempo máximo é um campo obrigatório.")
    @Positive(message = "O tempo máximo deve ser um número > 0.")
    private Integer tempoMaximo;

    @AssertTrue(message = "tempoMaximo não pode ser menor que tempoMinimo.")
    public boolean isTempoRangeValido() {
        if (tempoMinimo == null || tempoMaximo == null) return true;
        return tempoMaximo >= tempoMinimo;
    }
}
