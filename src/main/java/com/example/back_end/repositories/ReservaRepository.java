package com.example.back_end.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back_end.entities.ReservaEntity;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {
        boolean existsByClienteId(Integer clienteId);

        boolean existsByLocacaoId(Integer locacaoId);

        boolean existsByLocacaoIdAndDataInicioLessThanAndDataFimGreaterThan(
                        Integer locacaoId, LocalDate fimExclusivo, LocalDate inicioInclusivo);

        boolean existsByLocacaoIdAndIdNotAndDataInicioLessThanAndDataFimGreaterThan(
                        Integer locacaoId, Integer reservaId, LocalDate fimExclusivo, LocalDate inicioInclusivo);

        List<ReservaEntity> findDistinctByDataInicioLessThanAndDataFimGreaterThan(
                        LocalDate fimExclusivo, LocalDate inicioInclusivo);
}
