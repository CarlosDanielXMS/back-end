package com.example.back_end.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.back_end.dtos.reserva.ReservaGetDTO;
import com.example.back_end.dtos.reserva.ReservaPatchDTO;
import com.example.back_end.dtos.reserva.ReservaPostDTO;
import com.example.back_end.dtos.reserva.ReservaPutDTO;
import com.example.back_end.entities.ClienteEntity;
import com.example.back_end.entities.LocacaoEntity;
import com.example.back_end.entities.ReservaEntity;
import com.example.back_end.mappers.ReservaMapper;
import com.example.back_end.repositories.ClienteRepository;
import com.example.back_end.repositories.LocacaoRepository;
import com.example.back_end.repositories.ReservaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class ReservaService {
    private final ReservaRepository reservaRepo;
    private final LocacaoRepository locacaoRepo;
    private final ClienteRepository clienteRepo;
    private final ReservaMapper mapper;

    public Page<ReservaGetDTO> listarTodos(
            Pageable pageable) {
        return reservaRepo.findAll(pageable).map(mapper::toGetDTO);
    }

    public ReservaGetDTO buscarPorId(
            Integer id) {
        ReservaEntity reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada."));

        return mapper.toGetDTO(reserva);
    }

    public ReservaGetDTO criar(
            @Valid ReservaPostDTO dto) {
        ClienteEntity cliente = clienteRepo.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        LocacaoEntity locacao = locacaoRepo.findById(dto.getLocacaoId())
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada."));

        LocalDate inicio = dto.getDataInicio();
        LocalDate fim = dto.getDataFim();

        validarPeriodo(inicio, fim);
        long horas = calcularHoras(inicio, fim);
        validarJanelaComLocacao(locacao, horas);

        if (reservaRepo.existsByLocacaoIdAndDataInicioLessThanAndDataFimGreaterThan(
                locacao.getId(), fim, inicio)) {
            throw new IllegalArgumentException("Período indisponível: já existe reserva para essa locação.");
        }

        ReservaEntity reserva = new ReservaEntity();
        reserva.setCliente(cliente);
        reserva.setLocacao(locacao);
        reserva.setDataInicio(inicio);
        reserva.setDataFim(fim);
        reserva.setSituacao(dto.getSituacao());
        reserva.setValorFinal(calcularValorFinal(locacao, horas));

        reserva = reservaRepo.save(reserva);
        return mapper.toGetDTO(reserva);
    }

    public ReservaGetDTO atualizar(Integer id, @Valid ReservaPutDTO dto) {
        ReservaEntity existente = reservaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada."));

        ClienteEntity cliente = existente.getCliente();
        if (!existente.getCliente().getId().equals(dto.getClienteId())) {
            cliente = clienteRepo.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        }
        LocacaoEntity locacao = existente.getLocacao();
        if (!existente.getLocacao().getId().equals(dto.getLocacaoId())) {
            locacao = locacaoRepo.findById(dto.getLocacaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada."));
        }

        LocalDate inicio = dto.getDataInicio();
        LocalDate fim = dto.getDataFim();

        validarPeriodo(inicio, fim);
        long horas = calcularHoras(inicio, fim);
        validarJanelaComLocacao(locacao, horas);

        if (reservaRepo.existsByLocacaoIdAndIdNotAndDataInicioLessThanAndDataFimGreaterThan(
                locacao.getId(), existente.getId(), fim, inicio)) {
            throw new IllegalArgumentException("Período indisponível: já existe reserva para essa locação.");
        }

        existente.setCliente(cliente);
        existente.setLocacao(locacao);
        existente.setDataInicio(inicio);
        existente.setDataFim(fim);
        existente.setSituacao(dto.getSituacao());
        existente.setValorFinal(calcularValorFinal(locacao, horas));

        reservaRepo.save(existente);
        return mapper.toGetDTO(existente);
    }

    public ReservaGetDTO atualizarParcial(Integer id, @Valid ReservaPatchDTO patch) {
        var existente = reservaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada."));

        ClienteEntity cliente = existente.getCliente();
        if (patch.getClienteId() != null && !cliente.getId().equals(patch.getClienteId())) {
            cliente = clienteRepo.findById(patch.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        }
        LocacaoEntity locacao = existente.getLocacao();
        if (patch.getLocacaoId() != null && !locacao.getId().equals(patch.getLocacaoId())) {
            locacao = locacaoRepo.findById(patch.getLocacaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada."));
        }

        LocalDate inicio = patch.getDataInicio() != null ? patch.getDataInicio() : existente.getDataInicio();
        LocalDate fim = patch.getDataFim() != null ? patch.getDataFim() : existente.getDataFim();

        validarPeriodo(inicio, fim);
        long horas = calcularHoras(inicio, fim);
        validarJanelaComLocacao(locacao, horas);

        if (reservaRepo.existsByLocacaoIdAndIdNotAndDataInicioLessThanAndDataFimGreaterThan(
                locacao.getId(), existente.getId(), fim, inicio)) {
            throw new IllegalArgumentException("Período indisponível: já existe reserva para essa locação.");
        }

        existente.setCliente(cliente);
        existente.setLocacao(locacao);
        existente.setDataInicio(inicio);
        existente.setDataFim(fim);
        if (patch.getSituacao() != null)
            existente.setSituacao(patch.getSituacao());
        existente.setValorFinal(calcularValorFinal(locacao, horas));

        reservaRepo.save(existente);
        return mapper.toGetDTO(existente);
    }

    public void deletar(
            Integer id) {
        if (!reservaRepo.existsById(id)) {
            throw new EntityNotFoundException("Reserva não encontrada.");
        }

        reservaRepo.deleteById(id);
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Datas de início e fim são obrigatórias.");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("dataFim deve ser posterior a dataInicio.");
        }
    }

    private long calcularHoras(LocalDate inicio, LocalDate fim) {
        long dias = ChronoUnit.DAYS.between(inicio, fim);
        if (dias <= 0) {
            throw new IllegalArgumentException("dataFim deve ser maior que dataInicio.");
        }
        return dias * 24;
    }

    private void validarJanelaComLocacao(LocacaoEntity locacao, long horas) {
        if (locacao == null) {
            throw new IllegalArgumentException("Locação é obrigatória.");
        }
        if (horas < locacao.getTempoMinimo()) {
            throw new IllegalArgumentException("Quantidade de horas abaixo do tempo mínimo da locação.");
        }
        if (horas > locacao.getTempoMaximo()) {
            throw new IllegalArgumentException("Quantidade de horas acima do tempo máximo da locação.");
        }
    }

    private BigDecimal calcularValorFinal(LocacaoEntity locacao, long horas) {
        return locacao.getValorHora()
                .multiply(BigDecimal.valueOf(horas))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
