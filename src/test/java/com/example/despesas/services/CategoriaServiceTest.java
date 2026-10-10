package com.example.despesas.services;

import com.example.despesas.dtos.CategoriaRequestDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.repositories.CategoriaRepository;
import com.example.despesas.repositories.DespesaRepository;
import com.example.despesas.services.exceptions.DataBaseException;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void deveRetornarListaDeCategorias() {

        Categoria categoria = new Categoria(1L, "Lazer");
        Categoria categoria1 = new Categoria(2L, "Contas");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria, categoria1));

        List<Categoria> resultado = categoriaService.listarCategorias();

        assertEquals(2, resultado.size());
        assertSame(categoria, resultado.get(0));
        assertSame(categoria1, resultado.get(1));

    }

    @Test
    void deveRetornarListaVazia() {

        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<Categoria> resultado = categoriaService.listarCategorias();

        assertTrue(resultado.isEmpty());
        verify(categoriaRepository).findAll();

    }

    @Test
    void retornarCategoriaComIdExistente() {

        Categoria categoria = new Categoria(1L, "Lazer");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarCategoriaPorId(1L);

        assertSame(categoria, resultado);

    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExistir() {

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.buscarCategoriaPorId(99L)
        );

        verify(categoriaRepository).findById(99L);

    }

    @Test
    void deveCadastrarCategoria() {

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer");

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(chamada -> chamada.getArgument(0));
        Categoria resultado = categoriaService.cadastrarCategoria(dto);

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        Categoria categoriaCapturada = captor.getValue();

        assertSame(categoriaCapturada, resultado);
        assertEquals("Lazer", categoriaCapturada.getNome());

    }

    @Test
    void deveAtualizarCategoriaExistente() {

        Categoria categoria = new Categoria(1L, "Lazer");

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer no shopping");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria resultado = categoriaService.atualizarCategoria(1L, dto);

        assertSame(categoria, resultado);
        assertEquals(1L, categoria.getId());
        assertEquals("Lazer no shopping", categoria.getNome());

        verify(categoriaRepository).save(categoria);

    }

    @Test
    void deveLancarExcecaoAoTentarAtualizarCategoriaNaoExistente() {

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer no shopping");

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.atualizarCategoria(99L, dto)
        );

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any(Categoria.class));

    }

    @Test
    void deveDeletarCategoriaExistente() {

        Categoria categoria = new Categoria(1L, "Lazer");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.deletarCategoria(1L);

        verify(categoriaRepository).delete(categoria);

    }

    @Test
    void deveLancarExcecaoAoTentarExcluirCategoriaInexistente() {

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.deletarCategoria(99L)
        );

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoAoDeletarUmaCategoriaQueTemDespesaAssociada() {

        Categoria categoria = new Categoria(1L, "Lazer");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(despesaRepository.existsByCategoria_Id(categoria.getId())).thenReturn(true);

        assertThrows(
                DataBaseException.class,
                () -> categoriaService.deletarCategoria(1L)
        );

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository, never()).delete(categoria);

    }

    @Test
    void deveRetornarTrueSeCategoriaPossuiDespesas() {

        when(despesaRepository.existsByCategoria_Id(1L)).thenReturn(true);

        boolean resultado = categoriaService.possuiDespesas(1L);

        assertTrue(resultado);
        verify(despesaRepository).existsByCategoria_Id(1L);

    }

    @Test
    void deveRetornarFalseSeCategoriaNaoPossuiDespesas() {

        when(despesaRepository.existsByCategoria_Id(1L)).thenReturn(false);

        boolean resultado = categoriaService.possuiDespesas(1L);

        assertFalse(resultado);
        verify(despesaRepository).existsByCategoria_Id(1L);
    }

}
