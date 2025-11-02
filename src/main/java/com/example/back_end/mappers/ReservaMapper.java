package com.example.back_end.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.back_end.dtos.reserva.ReservaGetDTO;
import com.example.back_end.entities.ReservaEntity;

@Mapper(componentModel = "spring")
public interface ReservaMapper {
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "locacao.id", target = "locacaoId")
    ReservaGetDTO toGetDTO(ReservaEntity entity);

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "locacao.id", target = "locacaoId")
    List<ReservaGetDTO> toGetDTO(List<ReservaEntity> entities);
}
