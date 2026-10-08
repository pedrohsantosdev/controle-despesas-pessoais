package com.example.despesas.services;

import com.example.despesas.entities.Categoria;
import com.example.despesas.repositories.CategoriaRepository;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void deveRetornarCategoriaQuandoIdExistir() {

        Categoria categoria = new Categoria(1L, "Lazer");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarCategoriaPorId(1L);

        assertSame(categoria, resultado);

    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExistir() {

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.buscarCategoriaPorId(99L)
        );

    }

    void deveCadastrarCategoria() {

        Categoria categoria = new Categoria(1L, "Conta de luz");



    }

}
