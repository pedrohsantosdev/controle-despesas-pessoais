package com.example.despesas.dtos;

import com.example.despesas.entities.Categoria;

public record CategoriaResponseDTO(

        Long id,
        String nome
) {

    public CategoriaResponseDTO(Categoria categoria) {
        this(categoria.getId(), categoria.getNome());
    }
}
