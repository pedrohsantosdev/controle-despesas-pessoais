package com.example.despesas.services;

import com.example.despesas.entities.Despesa;
import com.example.despesas.repositories.DespesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepository;

    public DespesaService(DespesaRepository despensaRepository) {
        this.despesaRepository = despensaRepository;
    }

    public List<Despesa> listarDespesas() {
        return despesaRepository.findAll();
    }

    public Despesa buscarDespesaPorId(Long id) {
        Optional<Despesa> obj = despesaRepository.findById(id);
        return obj.get();
    }

    public Despesa cadastrarDespesa(Despesa obj) {
        return despesaRepository.save(obj);
    }
}
