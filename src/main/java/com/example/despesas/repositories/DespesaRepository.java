package com.example.despesas.repositories;

import com.example.despesas.entities.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    List<Despesa> findByPaga(Boolean paga);
    List<Despesa> findByPagaFalseAndDataVencimentoBefore(LocalDate data);
    List<Despesa> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);
    List<Despesa> findByCategoria_Id(Long categoriaId);
    List<Despesa> findByCategoria_IdAndPaga(Long categoriaId, Boolean paga);
    boolean existsByCategoria_Id(Long categoriaId);
}
