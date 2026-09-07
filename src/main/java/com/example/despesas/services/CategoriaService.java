package com.example.despesas.services;

import com.example.despesas.entities.Categoria;
import com.example.despesas.repositories.CategoriaRepository;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Categoria cadastrarCategoria(Categoria obj) {
        return categoriaRepository.save(obj);
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, Categoria novosDados) {
        Categoria obj = buscarCategoriaPorId(id);
        atualizarDados(obj, novosDados);
        return categoriaRepository.save(obj);
    }

    @Transactional
    public void deletarCategoria(Long id) {
        Categoria obj = buscarCategoriaPorId(id);
        categoriaRepository.delete(obj);
    }

    private void atualizarDados(Categoria obj, Categoria novosDados) {
        obj.setNome(novosDados.getNome());
    }
}
