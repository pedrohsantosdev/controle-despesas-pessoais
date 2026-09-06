package com.example.despesas.services;

import com.example.despesas.entities.Despesa;
import com.example.despesas.repositories.DespesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DespesaService {

    private final DespesaRepository despensaRepository;

    public DespesaService(DespesaRepository despensaRepository) {
        this.despensaRepository = despensaRepository;
    }

    public List<Despesa> listarDespesas() {
        return despensaRepository.findAll();
    }

    public Despesa buscarDespesaPorId(Long id) {
        Optional<Despesa> obj = despensaRepository.findById(id);
        return obj.get();
    }
}
