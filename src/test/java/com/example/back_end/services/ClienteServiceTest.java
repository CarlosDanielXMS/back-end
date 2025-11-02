package com.example.back_end.services;

import com.example.back_end.dtos.cliente.ClienteGetDTO;
import com.example.back_end.dtos.cliente.ClientePatchDTO;
import com.example.back_end.dtos.cliente.ClientePostDTO;
import com.example.back_end.dtos.cliente.ClientePutDTO;
import com.example.back_end.entities.ClienteEntity;
import com.example.back_end.mappers.ClienteMapper;
import com.example.back_end.repositories.ClienteRepository;
import com.example.back_end.repositories.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock private ClienteRepository clienteRepo;
    @Mock private ReservaRepository reservaRepo;
    @Mock private ClienteMapper mapper;

    @InjectMocks private ClienteService service;

    private ClienteEntity entity;
    private ClienteGetDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entity = new ClienteEntity(1, "Carlos", "carlos@mail.com", "11999999999", "12345678901", LocalDateTime.now());
        dto    = new ClienteGetDTO(1, "Carlos", "carlos@mail.com", "11999999999", "12345678901", entity.getDataCriacao());
    }

    @Test
    void listarTodos_deveRetornarPaginaDeDTOs() {
        when(clienteRepo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toGetDTO(entity)).thenReturn(dto);

        Page<ClienteGetDTO> page = service.listarTodos(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getEmail()).isEqualTo("carlos@mail.com");
        verify(clienteRepo).findAll(any(Pageable.class));
    }

    @Test
    void buscarPorId_existente_deveRetornarDTO() {
        when(clienteRepo.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toGetDTO(entity)).thenReturn(dto);

        ClienteGetDTO out = service.buscarPorId(1);

        assertThat(out.getId()).isEqualTo(1);
        verify(clienteRepo).findById(1);
    }

    @Test
    void buscarPorId_inexistente_deveLancarNotFound() {
        when(clienteRepo.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    void criar_deveSalvarEMapear() {
        ClientePostDTO in = new ClientePostDTO("Carlos","carlos@mail.com","11999999999","12345678901");
        when(mapper.fromPostDTO(in)).thenReturn(entity);
        when(clienteRepo.save(entity)).thenReturn(entity);
        when(mapper.toGetDTO(entity)).thenReturn(dto);

        ClienteGetDTO out = service.criar(in);

        assertThat(out.getEmail()).isEqualTo("carlos@mail.com");
        verify(clienteRepo).save(entity);
    }

    @Test
    void atualizar_deveAplicarPutEMapear() {
        ClientePutDTO put = new ClientePutDTO("Novo", "11888888888","10987654321");
        when(clienteRepo.findById(1)).thenReturn(Optional.of(entity));
        doAnswer(inv -> {
            ClientePutDTO p = inv.getArgument(0);
            ClienteEntity e = inv.getArgument(1);
            e.setNome(p.getNome());
            e.setTelefone(p.getTelefone());
            e.setCpf(p.getCpf());
            return null;
        }).when(mapper).updateFromPutDTO(eq(put), any(ClienteEntity.class));
        when(clienteRepo.save(any(ClienteEntity.class))).thenReturn(entity);
        when(mapper.toGetDTO(entity)).thenReturn(new ClienteGetDTO(1,"Novo","novo@mail.com","11888888888","10987654321", entity.getDataCriacao()));

        ClienteGetDTO out = service.atualizar(1, put);

        assertThat(out.getNome()).isEqualTo("Novo");
        assertThat(out.getEmail()).isEqualTo("novo@mail.com");
    }

    @Test
    void atualizarParcial_deveAplicarPatchIgnorandoNulls() {
        ClientePatchDTO patch = new ClientePatchDTO();
        patch.setNome("Parcial");
        when(clienteRepo.findById(1)).thenReturn(Optional.of(entity));
        doAnswer(inv -> {
            ClientePatchDTO p = inv.getArgument(0);
            ClienteEntity e = inv.getArgument(1);
            if (p.getNome()!=null) e.setNome(p.getNome());
            return null;
        }).when(mapper).updateFromPatchDTO(eq(patch), any(ClienteEntity.class));
        when(clienteRepo.save(any(ClienteEntity.class))).thenReturn(entity);
        when(mapper.toGetDTO(entity)).thenReturn(new ClienteGetDTO(1,"Parcial","carlos@mail.com","11999999999","12345678901", entity.getDataCriacao()));

        ClienteGetDTO out = service.atualizarParcial(1, patch);

        assertThat(out.getNome()).isEqualTo("Parcial");
    }

    @Test
    void deletar_comReservas_deveFalhar() {
        when(clienteRepo.existsById(1)).thenReturn(true);
        when(reservaRepo.existsByClienteId(1)).thenReturn(true);

        assertThatThrownBy(() -> service.deletar(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é possível deletar");
    }

    @Test
    void deletar_semReservas_deveRemover() {
        when(clienteRepo.existsById(1)).thenReturn(true);
        when(reservaRepo.existsByClienteId(1)).thenReturn(false);

        service.deletar(1);

        verify(clienteRepo).deleteById(1);
    }

    @Test
    void deletar_inexistente_deveFalhar() {
        when(clienteRepo.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> service.deletar(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
    }
}
