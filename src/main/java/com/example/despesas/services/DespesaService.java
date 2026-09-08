package com.example.despesas.services;

import com.example.despesas.dtos.ResumoDespesasDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.entities.Despesa;
import com.example.despesas.repositories.DespesaRepository;
import com.example.despesas.services.exceptions.InvalidDate;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final CategoriaService categoriaService;

    public DespesaService(DespesaRepository despesaRepository, CategoriaService categoriaService) {
        this.despesaRepository = despesaRepository;
        this.categoriaService = categoriaService;
    }

    public List<Despesa> listarDespesas(Boolean paga) {
        if(paga != null) {
            return despesaRepository.findByPaga(paga);
        }
        return despesaRepository.findAll();
    }

    public Despesa buscarDespesaPorId(Long id) {
        return despesaRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Despesa cadastrarDespesa(Despesa obj) {
        if(obj.getCategoria() != null) {
            Categoria cat = categoriaService.buscarCategoriaPorId(obj.getCategoria().getId());
            obj.setCategoria(cat);
        }
        return despesaRepository.save(obj);
    }

    @Transactional
    public Despesa atualizarDespesa(Long id, Despesa novosDados) {
        Despesa dadosAtuais = buscarDespesaPorId(id);

        if(novosDados.getCategoria() != null) {
            Categoria cat = categoriaService.buscarCategoriaPorId(novosDados.getCategoria().getId());
            novosDados.setCategoria(cat);
        }
        
        atualizarDados(dadosAtuais, novosDados);
        return despesaRepository.save(dadosAtuais);
    }

    private void atualizarDados(Despesa dadosAtuais, Despesa novosDados) {
        dadosAtuais.setDescricao(novosDados.getDescricao());
        dadosAtuais.setValor(novosDados.getValor());
        dadosAtuais.setDataVencimento(novosDados.getDataVencimento());
        dadosAtuais.setPaga(novosDados.isPaga());
        dadosAtuais.setCategoria(novosDados.getCategoria());
    }

    @Transactional
    public void deletarDespesa(Long id) {
        Despesa obj = buscarDespesaPorId(id);
        despesaRepository.delete(obj);
    }

    public List<Despesa> listarDespesasVencidas() {
        return despesaRepository.findByPagaFalseAndDataVencimentoBefore(LocalDate.now());
    }

    @Transactional
    public Despesa marcarDespesaComoPaga(Long id) {
        Despesa obj = buscarDespesaPorId(id);
        obj.setPaga(true);
        return despesaRepository.save(obj);
    }

    public List<Despesa> listarDespesasPorPeriodo(LocalDate inicio, LocalDate fim) {
        if(inicio.isAfter(fim)) {
            throw new InvalidDate("Data de início não deve estar após a data final");
        }
        return despesaRepository.findByDataVencimentoBetween(inicio, fim);
    }

    public ResumoDespesasDTO resumirDespesasPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Despesa> list = listarDespesasPorPeriodo(inicio, fim);
        double totalPago = 0.0;
        double totalPendende = 0.0;

        for(Despesa d : list) {
            if(d.isPaga()) {
                totalPago += d.getValor();
            }
            if(!d.isPaga()) {
                totalPendende += d.getValor();
            }
        }

        double total = totalPago + totalPendende;

        ResumoDespesasDTO obj = new ResumoDespesasDTO(total, totalPago, totalPendende);
        return obj;
    }
}
