package com.example.despesas.services;

import com.example.despesas.dtos.CategoriaRequestDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.repositories.CategoriaRepository;
import com.example.despesas.repositories.DespesaRepository;
import com.example.despesas.services.exceptions.DataBaseException;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final DespesaRepository despesaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, DespesaRepository despesaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.despesaRepository = despesaRepository;
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Categoria cadastrarCategoria(CategoriaRequestDTO dto) {
        Categoria obj = new Categoria(null, dto.nome());
        return categoriaRepository.save(obj);
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, CategoriaRequestDTO novosDados) {
        Categoria obj = buscarCategoriaPorId(id);
        atualizarDados(obj, novosDados);
        return categoriaRepository.save(obj);
    }

    @Transactional
    public void deletarCategoria(Long id) {
            Categoria obj = buscarCategoriaPorId(id);
            if(despesaRepository.existsByCategoria_Id(obj.getId())) {
                throw new DataBaseException("Não é possível excluir uma categoria que possui despesas associadas.");
            }
            categoriaRepository.delete(obj);
    }

    private void atualizarDados(Categoria obj, CategoriaRequestDTO novosDados) {
        obj.setNome(novosDados.nome());
    }

    public boolean possuiDespesas(Long id) {
        return despesaRepository.existsByCategoria_Id(id);
    }
}
