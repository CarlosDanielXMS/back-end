package com.example.back_end.dtos.reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.back_end.enums.SituacaoReserva;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "ReservaGetDTO", description = "Representação de reserva para leitura")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservaGetDTO {

    @Schema(description = "ID da reserva", example = "1")
    private Integer id;

    @Schema(description = "ID do cliente", example = "1")
    private Integer clienteId;

    @Schema(description = "ID da locação", example = "1")
    private Integer locacaoId;

    @Schema(description = "Data de início", type = "string", format = "date", example = "2025-10-30")
    private LocalDate dataInicio;

    @Schema(description = "Data de fim", type = "string", format = "date", example = "2025-10-31")
    private LocalDate dataFim;

    @Schema(description = "Valor final da reserva", example = "480.00")
    private BigDecimal valorFinal;

    @Schema(description = "Situação da reserva", implementation = SituacaoReserva.class, example = "CONFIRMADA")
    private SituacaoReserva situacao;

    @Schema(description = "Data/hora de criação (ISO-8601)", type = "string", format = "date-time",
            example = "2025-10-31T14:23:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
}
