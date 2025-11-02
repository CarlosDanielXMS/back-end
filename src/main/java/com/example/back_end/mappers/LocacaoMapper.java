package com.example.back_end.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.back_end.dtos.locacao.LocacaoGetDTO;
import com.example.back_end.dtos.locacao.LocacaoPatchDTO;
import com.example.back_end.dtos.locacao.LocacaoPostDTO;
import com.example.back_end.dtos.locacao.LocacaoPutDTO;
import com.example.back_end.entities.LocacaoEntity;

@Mapper(componentModel = "spring")
public interface LocacaoMapper {

    LocacaoGetDTO toGetDTO(LocacaoEntity entity);

    List<LocacaoGetDTO> toGetDTO(List<LocacaoEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    LocacaoEntity fromPostDTO(LocacaoPostDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    void updateFromPutDTO(LocacaoPutDTO dto, @MappingTarget LocacaoEntity target);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    void updateFromPatchDTO(LocacaoPatchDTO dto, @MappingTarget LocacaoEntity target);
}
