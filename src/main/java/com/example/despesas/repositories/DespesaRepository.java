package com.example.despesas.repositories;

import com.example.despesas.entities.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    List<Despesa> findByPaga(Boolean paga);
}
