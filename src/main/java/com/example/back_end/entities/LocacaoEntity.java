package com.example.back_end.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import com.example.back_end.enums.TiposLocacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locacoes")
@Check(constraints = """
            tempo_minimo > 0 AND
            tempo_maximo > 0 AND
            tempo_maximo >= tempo_minimo AND
            valor_hora > 0
        """)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TiposLocacao tipo;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "valor_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorHora;

    @Column(name = "tempo_minimo", nullable = false)
    private Integer tempoMinimo;

    @Column(name = "tempo_maximo", nullable = false)
    private Integer tempoMaximo;

    @Column(name = "data_criacao", updatable = false)
    @CreationTimestamp
    private LocalDateTime dataCriacao;
}
