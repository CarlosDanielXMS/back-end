package com.example.back_end.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.back_end.dtos.locacao.LocacaoGetDTO;
import com.example.back_end.dtos.locacao.LocacaoPatchDTO;
import com.example.back_end.dtos.locacao.LocacaoPostDTO;
import com.example.back_end.dtos.locacao.LocacaoPutDTO;
import com.example.back_end.entities.LocacaoEntity;
import com.example.back_end.entities.ReservaEntity;
import com.example.back_end.mappers.LocacaoMapper;
import com.example.back_end.repositories.LocacaoRepository;
import com.example.back_end.repositories.ReservaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class LocacaoService {
    private final LocacaoRepository locacaoRepo;
    private final ReservaRepository reservaRepo;
    private final LocacaoMapper mapper;

    public Page<LocacaoGetDTO> listarTodos(
            Pageable pageable) {
        return locacaoRepo.findAll(pageable).map(mapper::toGetDTO);
    }

    public LocacaoGetDTO buscarPorId(
            Integer id) {
        LocacaoEntity locacao = locacaoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada."));

        return mapper.toGetDTO(locacao);
    }

    public Page<LocacaoGetDTO> listarDisponiveis(LocalDate data, Pageable pageable) {
        return listarDisponiveis(data, data.plusDays(1), pageable);
    }

    public Page<LocacaoGetDTO> listarDisponiveis(LocalDate inicio, LocalDate fim, Pageable pageable) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Parâmetros 'inicio' e 'fim' são obrigatórios.");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("'fim' deve ser posterior a 'inicio'.");
        }

        long horasLong = java.time.temporal.ChronoUnit.DAYS.between(inicio, fim) * 24L;
        int horas = Math.toIntExact(horasLong);

        List<ReservaEntity> reservasConflitantes = reservaRepo
                .findDistinctByDataInicioLessThanAndDataFimGreaterThan(fim, inicio);

        List<Integer> ocupadas = reservasConflitantes.stream()
                .map(r -> r.getLocacao().getId())
                .distinct()
                .toList();

        Page<LocacaoEntity> page;
        if (ocupadas.isEmpty()) {
            page = locacaoRepo.findByTempoMinimoLessThanEqualAndTempoMaximoGreaterThanEqual(horas, horas, pageable);
        } else {
            page = locacaoRepo.findByIdNotInAndTempoMinimoLessThanEqualAndTempoMaximoGreaterThanEqual(
                    ocupadas, horas, horas, pageable);
        }

        return page.map(mapper::toGetDTO);
    }

    public LocacaoGetDTO criar(
            @Valid LocacaoPostDTO novo) {
        LocacaoEntity locacao = mapper.fromPostDTO(novo);

        locacao = locacaoRepo.save(locacao);
        return mapper.toGetDTO(locacao);
    }

    public LocacaoGetDTO atualizar(
            Integer id,
            @Valid LocacaoPutDTO atualizado) {
        LocacaoEntity existente = locacaoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada."));

        mapper.updateFromPutDTO(atualizado, existente);

        existente = locacaoRepo.save(existente);
        return mapper.toGetDTO(existente);
    }

    public LocacaoGetDTO atualizarParcial(
            Integer id,
            @Valid LocacaoPatchDTO atualizado) {
        LocacaoEntity existente = locacaoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada."));

        mapper.updateFromPatchDTO(atualizado, existente);

        existente = locacaoRepo.save(existente);
        return mapper.toGetDTO(existente);
    }

    public void deletar(
            Integer id) {
        if (!locacaoRepo.existsById(id)) {
            throw new EntityNotFoundException("Locação não encontrada.");
        }
        if (reservaRepo.existsByLocacaoId(id)) {
            throw new RuntimeException("Não pe possível deletar uma Locação que já tenha sido reservada.");
        }

        locacaoRepo.deleteById(id);
    }
}
