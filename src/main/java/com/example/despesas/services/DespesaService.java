package com.example.despesas.services;

import com.example.despesas.dtos.DespesaRequestDTO;
import com.example.despesas.dtos.DespesaUpdateDTO;
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

    public List<Despesa> listarDespesas(Boolean paga, Long categoriaId) {
        if(categoriaId != null && paga != null ) {
            return despesaRepository.findByCategoria_IdAndPaga(categoriaId, paga);
        }
        if(paga != null) {
            return despesaRepository.findByPaga(paga);
        }
        if(categoriaId != null) {
            return despesaRepository.findByCategoria_Id(categoriaId);
        }
        return despesaRepository.findAll();
    }

    public Despesa buscarDespesaPorId(Long id) {
        return despesaRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Despesa cadastrarDespesa(DespesaRequestDTO dto) {
        Despesa despesa = new Despesa(null, dto.descricao(), dto.valor(), dto.dataVencimento(), dto.paga());

        if(despesa.getCategoria() != null) {
            Categoria categoria = categoriaService.buscarCategoriaPorId(dto.categoriaId());
            despesa.setCategoria(categoria);
        }
        return despesaRepository.save(despesa);
    }

    @Transactional
    public Despesa atualizarDespesa(Long id, DespesaUpdateDTO novosDados) {
        Despesa dadosAtuais = buscarDespesaPorId(id);

        Categoria categoria = null;

        if(novosDados.categoriaId() != null) {
            categoria = categoriaService.buscarCategoriaPorId(novosDados.categoriaId());
        }

        atualizarDados(dadosAtuais, novosDados, categoria);
        return despesaRepository.save(dadosAtuais);
    }

    private void atualizarDados(Despesa dadosAtuais, DespesaUpdateDTO novosDados, Categoria categoria) {
        dadosAtuais.setDescricao(novosDados.descricao());
        dadosAtuais.setValor(novosDados.valor());
        dadosAtuais.setDataVencimento(novosDados.dataVencimento());
        dadosAtuais.setCategoria(categoria);
    }

    @Transactional
    public void deletarDespesa(Long id) {
        Despesa despesa = buscarDespesaPorId(id);
        despesaRepository.delete(despesa);
    }

    public List<Despesa> listarDespesasVencidas() {
        return despesaRepository.findByPagaFalseAndDataVencimentoBefore(LocalDate.now());
    }

    @Transactional
    public Despesa marcarDespesaComoPaga(Long id) {
        Despesa despesa = buscarDespesaPorId(id);
        despesa.setPaga(true);
        return despesaRepository.save(despesa);
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

        ResumoDespesasDTO resumo = new ResumoDespesasDTO(total, totalPago, totalPendende);
        return resumo;
    }
}
