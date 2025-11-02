package com.example.back_end.dtos.reserva;

import java.time.LocalDate;

import com.example.back_end.enums.SituacaoReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "ReservaPutDTO", description = "Dados para atualização total de reserva")
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class ReservaPutDTO {

    @Schema(description = "ID do cliente", example = "1")
    @NotNull(message = "O cliente é um campo obrigatório.")
    @Positive(message = "Não existe um cliente com id <= 0.")
    private Integer clienteId;

    @Schema(description = "ID da locação", example = "1")
    @NotNull(message = "A locação é um campo obrigatório.")
    @Positive(message = "Não existe uma locação com id <= 0.")
    private Integer locacaoId;

    @Schema(description = "Data de início", type = "string", format = "date", example = "2025-10-30")
    @NotNull(message = "A data de início é um campo obrigatório.")
    private LocalDate dataInicio;

    @Schema(description = "Data de fim", type = "string", format = "date", example = "2025-10-31")
    @NotNull(message = "A data de fim é um campo obrigatório.")
    private LocalDate dataFim;

    @Schema(description = "Situação da reserva", implementation = SituacaoReserva.class, example = "CONFIRMADA")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "A situação da reserva é um campo obrigatório.")
    private SituacaoReserva situacao;

    @AssertTrue(message = "A data do fim da Reserva deve ser posterior à de início.")
    public boolean isIntervaloValido() {
        if (dataInicio == null || dataFim == null) return true;
        return dataFim.isAfter(dataInicio);
    }
}
