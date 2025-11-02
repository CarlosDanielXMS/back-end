package com.example.back_end.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.back_end.dtos.cliente.ClienteGetDTO;
import com.example.back_end.dtos.cliente.ClientePatchDTO;
import com.example.back_end.dtos.cliente.ClientePostDTO;
import com.example.back_end.dtos.cliente.ClientePutDTO;
import com.example.back_end.entities.ClienteEntity;
import com.example.back_end.mappers.ClienteMapper;
import com.example.back_end.repositories.ClienteRepository;
import com.example.back_end.repositories.ReservaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class ClienteService {
    private final ClienteRepository clienteRepo;
    private final ReservaRepository reservaRepo;
    private final ClienteMapper mapper;

    public Page<ClienteGetDTO> listarTodos(
            Pageable pageable) {
        return clienteRepo.findAll(pageable).map(mapper::toGetDTO);
    }

    public ClienteGetDTO buscarPorId(
            Integer id) {
        ClienteEntity cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        return mapper.toGetDTO(cliente);
    }

    public ClienteGetDTO criar(
            @Valid ClientePostDTO novo) {
        ClienteEntity cliente = mapper.fromPostDTO(novo);

        cliente = clienteRepo.save(cliente);
        return mapper.toGetDTO(cliente);
    }

    public ClienteGetDTO atualizar(
            Integer id,
            @Valid ClientePutDTO atualizado) {
        ClienteEntity existente = clienteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        mapper.updateFromPutDTO(atualizado, existente);

        existente = clienteRepo.save(existente);
        return mapper.toGetDTO(existente);
    }

    public ClienteGetDTO atualizarParcial(
            Integer id,
            @Valid ClientePatchDTO atualizado) {
        ClienteEntity existente = clienteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        mapper.updateFromPatchDTO(atualizado, existente);

        existente = clienteRepo.save(existente);
        return mapper.toGetDTO(existente);
    }

    public void deletar(
            Integer id) {
        if (!clienteRepo.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado.");
        }
        if (reservaRepo.existsByClienteId(id)) {
            throw new RuntimeException("Não é possível deletar um Cliente que já tenha feito alguma reserva.");
        }

        clienteRepo.deleteById(id);
    }
}
