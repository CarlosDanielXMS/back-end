package com.example.back_end.services;

import com.example.back_end.dtos.reserva.ReservaGetDTO;
import com.example.back_end.dtos.reserva.ReservaPatchDTO;
import com.example.back_end.dtos.reserva.ReservaPostDTO;
import com.example.back_end.dtos.reserva.ReservaPutDTO;
import com.example.back_end.entities.ClienteEntity;
import com.example.back_end.entities.LocacaoEntity;
import com.example.back_end.entities.ReservaEntity;
import com.example.back_end.enums.SituacaoReserva;
import com.example.back_end.enums.TiposLocacao;
import com.example.back_end.mappers.ReservaMapper;
import com.example.back_end.repositories.ClienteRepository;
import com.example.back_end.repositories.LocacaoRepository;
import com.example.back_end.repositories.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
    import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepo;
    @Mock private LocacaoRepository locacaoRepo;
    @Mock private ClienteRepository clienteRepo;
    @Mock private ReservaMapper mapper;

    @InjectMocks private ReservaService service;

    private ClienteEntity cliente;
    private LocacaoEntity locacao;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cliente = new ClienteEntity(1,"Carlos","c@mail.com","119","12345678901", null);
        locacao = new LocacaoEntity(1,"Sala", TiposLocacao.TEMPORADA,"", new BigDecimal("10.00"), 1, 48, null);
    }

    @Test
    void criar_deveValidarConflito() {
        ReservaPostDTO dto = new ReservaPostDTO(1,1, LocalDate.now(), LocalDate.now().plusDays(1), SituacaoReserva.CONFIRMADA);

        when(clienteRepo.findById(1)).thenReturn(Optional.of(cliente));
        when(locacaoRepo.findById(1)).thenReturn(Optional.of(locacao));
        when(reservaRepo.existsByLocacaoIdAndDataInicioLessThanAndDataFimGreaterThan(eq(1), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Período indisponível");
    }

    @Test
    void criar_ok_semConflito() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim    = inicio.plusDays(1);
        ReservaPostDTO dto = new ReservaPostDTO(1,1, inicio, fim, SituacaoReserva.CONFIRMADA);

        when(clienteRepo.findById(1)).thenReturn(Optional.of(cliente));
        when(locacaoRepo.findById(1)).thenReturn(Optional.of(locacao));
        when(reservaRepo.existsByLocacaoIdAndDataInicioLessThanAndDataFimGreaterThan(1, fim, inicio))
                .thenReturn(false);

        // simula salvar
        ReservationCapture cap = new ReservationCapture();
        when(reservaRepo.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity r = inv.getArgument(0);
            cap.entity = r;
            r.setId(10);
            return r;
        });

        ReservaGetDTO saida = new ReservaGetDTO(10, 1, 1, inicio, fim, new BigDecimal("240.00"), SituacaoReserva.CONFIRMADA, null);
        when(mapper.toGetDTO(any(ReservaEntity.class))).thenReturn(saida);

        ReservaGetDTO out = service.criar(dto);

        assertThat(out.getId()).isEqualTo(10);
        assertThat(cap.entity.getValorFinal()).isEqualByComparingTo(new BigDecimal("240.00"));
    }

    @Test
    void atualizar_deveTrocarClienteELocacao_ValidandoConflito() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim    = inicio.plusDays(2);

        ReservaEntity existente = new ReservaEntity();
        existente.setId(5);
        existente.setCliente(cliente);
        existente.setLocacao(locacao);
        existente.setDataInicio(inicio);
        existente.setDataFim(fim);

        ClienteEntity novoCliente = new ClienteEntity(2,"Ana","ana@mail.com","118","22222222222",null);
        LocacaoEntity novaLocacao = new LocacaoEntity(2,"Sala B", TiposLocacao.TEMPORADA,"", new BigDecimal("20.00"), 24, 72, null);

        when(reservaRepo.findById(5)).thenReturn(Optional.of(existente));
        when(clienteRepo.findById(2)).thenReturn(Optional.of(novoCliente));
        when(locacaoRepo.findById(2)).thenReturn(Optional.of(novaLocacao));
        when(reservaRepo.existsByLocacaoIdAndIdNotAndDataInicioLessThanAndDataFimGreaterThan(eq(2), eq(5), any(), any()))
                .thenReturn(false);
        when(reservaRepo.save(any(ReservaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toGetDTO(any(ReservaEntity.class))).thenReturn(
                new ReservaGetDTO(5, 2, 2, inicio, fim, new BigDecimal("960.00"), SituacaoReserva.CONFIRMADA, null)
        );

        ReservaPutDTO put = new ReservaPutDTO(2, 2, inicio, fim, SituacaoReserva.CONFIRMADA);
        ReservaGetDTO out = service.atualizar(5, put);

        assertThat(out.getClienteId()).isEqualTo(2);
        assertThat(out.getLocacaoId()).isEqualTo(2);
        assertThat(out.getValorFinal()).isEqualByComparingTo(new BigDecimal("960.00"));
    }

    @Test
    void atualizarParcial_deveValidarRangeETempoMinMax() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim    = inicio.plusDays(1);

        ReservaEntity existente = new ReservaEntity();
        existente.setId(7);
        existente.setCliente(cliente);
        existente.setLocacao(locacao);
        existente.setDataInicio(inicio);
        existente.setDataFim(fim);

        when(reservaRepo.findById(7)).thenReturn(Optional.of(existente));

        ReservaPatchDTO patch = new ReservaPatchDTO();
        patch.setDataFim(inicio.plusDays(3));

        assertThatThrownBy(() -> service.atualizarParcial(7, patch))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("acima do tempo máximo");
    }

    @Test
    void deletar_inexistente_deveFalhar() {
        when(reservaRepo.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> service.deletar(99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada");
    }

    private static class ReservationCapture { ReservaEntity entity; }
}
