package com.example.back_end.services;

import com.example.back_end.dtos.locacao.LocacaoGetDTO;
import com.example.back_end.dtos.locacao.LocacaoPatchDTO;
import com.example.back_end.dtos.locacao.LocacaoPostDTO;
import com.example.back_end.dtos.locacao.LocacaoPutDTO;
import com.example.back_end.entities.LocacaoEntity;
import com.example.back_end.entities.ReservaEntity;
import com.example.back_end.enums.TiposLocacao;
import com.example.back_end.mappers.LocacaoMapper;
import com.example.back_end.repositories.LocacaoRepository;
import com.example.back_end.repositories.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocacaoServiceTest {

    @Mock private LocacaoRepository locacaoRepo;
    @Mock private ReservaRepository reservaRepo;
    @Mock private LocacaoMapper mapper;

    @InjectMocks private LocacaoService service;

    private LocacaoEntity loc;
    private LocacaoGetDTO dto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        loc = new LocacaoEntity(1, "Sala A", TiposLocacao.RESIDENCIAL, "Desc", new BigDecimal("10.00"), 1, 48, null);
        dto = new LocacaoGetDTO(1, "Sala A", TiposLocacao.RESIDENCIAL, "Desc", new BigDecimal("10.00"), 1, 48, null);
    }

    @Test
    void listarTodos_ok() {
        when(locacaoRepo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(loc)));
        when(mapper.toGetDTO(loc)).thenReturn(dto);

        Page<LocacaoGetDTO> page = service.listarTodos(PageRequest.of(0,10));

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void buscarPorId_existente_ok() {
        when(locacaoRepo.findById(1)).thenReturn(Optional.of(loc));
        when(mapper.toGetDTO(loc)).thenReturn(dto);

        LocacaoGetDTO out = service.buscarPorId(1);

        assertThat(out.getId()).isEqualTo(1);
    }

    @Test
    void buscarPorId_inexistente_404() {
        when(locacaoRepo.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Locação não encontrada");
    }

    @Test
    void disponiveisEntre_semConflito_retornaLista() {
        LocalDate inicio = LocalDate.of(2025,1,1);
        LocalDate fim    = LocalDate.of(2025,1,2); // 24h
        when(reservaRepo.findDistinctByDataInicioLessThanAndDataFimGreaterThan(fim, inicio))
                .thenReturn(List.of());
        when(locacaoRepo.findByTempoMinimoLessThanEqualAndTempoMaximoGreaterThanEqual(eq(24), eq(24), any()))
                .thenReturn(new PageImpl<>(List.of(loc)));
        when(mapper.toGetDTO(loc)).thenReturn(dto);

        Page<LocacaoGetDTO> page = service.listarDisponiveis(inicio, fim, PageRequest.of(0,10));

        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void disponiveisEntre_ocupadas_filtraIds() {
        LocalDate inicio = LocalDate.of(2025,1,1);
        LocalDate fim    = LocalDate.of(2025,1,4);
        ReservaEntity r1 = new ReservaEntity(); r1.setLocacao(loc);
        when(reservaRepo.findDistinctByDataInicioLessThanAndDataFimGreaterThan(fim, inicio))
                .thenReturn(List.of(r1));
        when(locacaoRepo.findByIdNotInAndTempoMinimoLessThanEqualAndTempoMaximoGreaterThanEqual(
                anyList(), eq(72), eq(72), any()))
                .thenReturn(new PageImpl<>(List.of()));

        Page<LocacaoGetDTO> page = service.listarDisponiveis(inicio, fim, PageRequest.of(0,10));

        assertThat(page.getContent()).isEmpty();
    }

    @Test
    void disponiveisEntre_parametrosInvalidos_deveLancar() {
        LocalDate inicio = LocalDate.of(2025,1,2);
        LocalDate fim    = LocalDate.of(2025,1,1);
        assertThatThrownBy(() -> service.listarDisponiveis(inicio, fim, PageRequest.of(0,10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'fim' deve ser posterior a 'inicio'");
    }

    @Test
    void criar_ok() {
        LocacaoPostDTO in = new LocacaoPostDTO("Sala A", TiposLocacao.RESIDENCIAL, "Desc", new BigDecimal("10.00"), 1, 48);
        when(mapper.fromPostDTO(in)).thenReturn(loc);
        when(locacaoRepo.save(loc)).thenReturn(loc);
        when(mapper.toGetDTO(loc)).thenReturn(dto);

        LocacaoGetDTO out = service.criar(in);

        assertThat(out.getNome()).isEqualTo("Sala A");
    }

    @Test
    void atualizar_ok() {
        LocacaoPutDTO put = new LocacaoPutDTO("Sala B", TiposLocacao.NAO_RESIDENCIAL, "Nova", new BigDecimal("12.00"), 2, 36);
        when(locacaoRepo.findById(1)).thenReturn(Optional.of(loc));
        doAnswer(inv -> {
            LocacaoPutDTO p = inv.getArgument(0);
            LocacaoEntity e = inv.getArgument(1);
            e.setNome(p.getNome());
            e.setDescricao(p.getDescricao());
            e.setValorHora(p.getValorHora());
            e.setTempoMinimo(p.getTempoMinimo());
            e.setTempoMaximo(p.getTempoMaximo());
            e.setTipo(p.getTipo());
            return null;
        }).when(mapper).updateFromPutDTO(eq(put), any(LocacaoEntity.class));
        when(locacaoRepo.save(any(LocacaoEntity.class))).thenReturn(loc);
        when(mapper.toGetDTO(loc)).thenReturn(new LocacaoGetDTO(1,"Sala B", TiposLocacao.NAO_RESIDENCIAL,"Nova", new BigDecimal("12.00"), 2, 36, null));

        LocacaoGetDTO out = service.atualizar(1, put);

        assertThat(out.getNome()).isEqualTo("Sala B");
        assertThat(out.getTempoMaximo()).isEqualTo(36);
    }

    @Test
    void atualizarParcial_ok() {
        LocacaoPatchDTO patch = new LocacaoPatchDTO();
        patch.setDescricao("Patch");
        when(locacaoRepo.findById(1)).thenReturn(Optional.of(loc));
        doAnswer(inv -> {
            LocacaoPatchDTO p = inv.getArgument(0);
            LocacaoEntity e = inv.getArgument(1);
            if (p.getDescricao()!=null) e.setDescricao(p.getDescricao());
            return null;
        }).when(mapper).updateFromPatchDTO(eq(patch), any(LocacaoEntity.class));
        when(locacaoRepo.save(any(LocacaoEntity.class))).thenReturn(loc);
        when(mapper.toGetDTO(loc)).thenReturn(new LocacaoGetDTO(1,"Sala A", TiposLocacao.TEMPORADA,"Patch", new BigDecimal("10.00"),1,48,null));

        LocacaoGetDTO out = service.atualizarParcial(1, patch);

        assertThat(out.getDescricao()).isEqualTo("Patch");
    }

    @Test
    void deletar_comReserva_deveFalhar() {
        when(locacaoRepo.existsById(1)).thenReturn(true);
        when(reservaRepo.existsByLocacaoId(1)).thenReturn(true);

        assertThatThrownBy(() -> service.deletar(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não pe possível");
    }

    @Test
    void deletar_semReserva_ok() {
        when(locacaoRepo.existsById(1)).thenReturn(true);
        when(reservaRepo.existsByLocacaoId(1)).thenReturn(false);

        service.deletar(1);

        verify(locacaoRepo).deleteById(1);
    }

    @Test
    void deletar_inexistente_404() {
        when(locacaoRepo.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> service.deletar(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Locação não encontrada");
    }
}
