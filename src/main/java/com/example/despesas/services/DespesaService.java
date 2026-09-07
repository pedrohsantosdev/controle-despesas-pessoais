package com.example.despesas.services;

import com.example.despesas.entities.Despesa;
import com.example.despesas.repositories.DespesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepository;

    public DespesaService(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
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

    public Despesa atualizarDespesa(Long id, Despesa novosDados) {
        Despesa dadosAtuais = despesaRepository.getReferenceById(id);
        atualizarDados(dadosAtuais, novosDados);
        return despesaRepository.save(dadosAtuais);
    }

    private void atualizarDados(Despesa dadosAtuais, Despesa novosDados) {
        dadosAtuais.setDescricao(novosDados.getDescricao());
        dadosAtuais.setValor(novosDados.getValor());
        dadosAtuais.setDataVencimento(novosDados.getDataVencimento());
        dadosAtuais.setPaga(novosDados.getPaga());
    }

    public void deletarDespesa(Long id) {
        despesaRepository.deleteById(id);
    }
}
