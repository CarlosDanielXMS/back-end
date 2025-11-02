package com.example.back_end.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.back_end.dtos.cliente.ClienteGetDTO;
import com.example.back_end.dtos.cliente.ClientePatchDTO;
import com.example.back_end.dtos.cliente.ClientePostDTO;
import com.example.back_end.dtos.cliente.ClientePutDTO;
import com.example.back_end.entities.ClienteEntity;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteGetDTO toGetDTO(ClienteEntity entity);

    List<ClienteGetDTO> toGetDTO(List<ClienteEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    ClienteEntity fromPostDTO(ClientePostDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateFromPutDTO(ClientePutDTO dto, @MappingTarget ClienteEntity target);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateFromPatchDTO(ClientePatchDTO dto, @MappingTarget ClienteEntity target);
}
