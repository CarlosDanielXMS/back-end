package com.example.back_end.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back_end.entities.LocacaoEntity;

@Repository
public interface LocacaoRepository extends JpaRepository<LocacaoEntity, Integer> {
        Page<LocacaoEntity> findByIdNotIn(List<Integer> ids, Pageable pageable);

        Page<LocacaoEntity> findByTempoMinimoLessThanEqualAndTempoMaximoGreaterThanEqual(
                        Integer horasMinIncl, Integer horasMaxIncl, Pageable pageable);

        Page<LocacaoEntity> findByIdNotInAndTempoMinimoLessThanEqualAndTempoMaximoGreaterThanEqual(
                        List<Integer> ids, Integer horasMinIncl, Integer horasMaxIncl, Pageable pageable);
}
