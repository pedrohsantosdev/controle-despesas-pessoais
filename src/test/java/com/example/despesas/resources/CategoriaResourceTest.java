package com.example.despesas.resources;

import com.example.despesas.dtos.CategoriaRequestDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.services.CategoriaService;
import com.example.despesas.services.exceptions.DataBaseException;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaResource.class)
@ActiveProfiles("test")
public class CategoriaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @Test
    void deveRetornar200paraListarCategoriasCadastradas() throws Exception {

        Categoria c1 = new Categoria(1L, "Lazer");
        Categoria c2 = new Categoria(2L, "Contas residenciais");

        when(categoriaService.listarCategorias()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/categorias")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(categoriaService).listarCategorias();

    }

    @Test
    void deveRetornar200quandoListaDeCategoriasForVazia() throws Exception {

        when(categoriaService.listarCategorias()).thenReturn(List.of());

        mockMvc.perform(get("/categorias")).andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(categoriaService).listarCategorias();
    }

    @Test
    void deveRetornar200paraBuscaDeCategoriaComIdExistente() throws Exception {

        Categoria c1 = new Categoria(1L, "Lazer");

        when(categoriaService.buscarCategoriaPorId(1L)).thenReturn(c1);

        mockMvc.perform(get("/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(categoriaService).buscarCategoriaPorId(1L);

    }

    @Test
    void deveRetornar404paraBuscaDeCategoriaComIdInexistente() throws Exception {

        when(categoriaService.buscarCategoriaPorId(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(get("/categorias/{id}", 99L)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/categorias/99"));

        verify(categoriaService).buscarCategoriaPorId(99L);

    }

    @Test
    void deveRetornar201paraCategoriaCadastrada() throws Exception {

        Categoria c1 = new Categoria(1L, "Lazer");

        when(categoriaService.cadastrarCategoria(any(CategoriaRequestDTO.class))).thenReturn(c1);

        String json = """
                {
                "nome": "Lazer"
                }
                """;

        mockMvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/categorias/1"))
                .andExpect(jsonPath("$.id").value(1L));

        ArgumentCaptor<CategoriaRequestDTO> captor = ArgumentCaptor.forClass(CategoriaRequestDTO.class);
        verify(categoriaService).cadastrarCategoria(captor.capture());
        CategoriaRequestDTO dto = captor.getValue();

        assertEquals(c1.getNome(), dto.nome());

    }

    @Test
    void deveRetornar400aoTentarCadastrarCategoriaComNomeNulo() throws Exception {

        String json = """
                {
                "nome": null
                }
                """;

        mockMvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("nome: Nome da categoria é obrigatório"));

        verify(categoriaService, never()).cadastrarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void deveRetornar400aoTentarCadastrarCategoriaComNomeVazio() throws Exception {

        String json = """
                {
                "nome": ""
                }
                """;

        mockMvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("nome: Nome da categoria é obrigatório"));

        verify(categoriaService, never()).cadastrarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void deveRetornar400aoTentarCadastrarCategoriaComNomeSomenteComEspacos() throws Exception {

        String json = """
                {
                "nome": "  "
                }
                """;

        mockMvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("nome: Nome da categoria é obrigatório"));

        verify(categoriaService, never()).cadastrarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void deveRetornar200aoAtualizarCategoriaComIdExistenteEdadosValidos() throws Exception {

        Categoria c1 = new Categoria(1L, "Contas residenciais");

        when(categoriaService.atualizarCategoria(eq(1L), any(CategoriaRequestDTO.class))).thenReturn(c1);

        String json = """
                {
                "nome" : "Contas residenciais"
                }
                """;


        mockMvc.perform(put("/categorias/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Contas residenciais"));

        ArgumentCaptor<CategoriaRequestDTO> captor = ArgumentCaptor.forClass(CategoriaRequestDTO.class);
        verify(categoriaService).atualizarCategoria(eq(1L), captor.capture());
        CategoriaRequestDTO dto = captor.getValue();

        assertEquals("Contas residenciais", dto.nome());

    }

    @Test
    void deveRetornar404aoTentarAtualizarCategoriaComIdInexistente() throws Exception {

        String json = """
                {
                "nome" : "Contas residenciais"
                }
                """;

        when(categoriaService.atualizarCategoria(eq(99L), any(CategoriaRequestDTO.class))).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(put("/categorias/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/categorias/99"));

        verify(categoriaService).atualizarCategoria(eq(99L), any(CategoriaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoTentarAtualizarCategoriaComNomeNulo() throws Exception {

        String json = """
                {
                "nome" : null
                }
                """;

        mockMvc.perform(put("/categorias/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("nome: Nome da categoria é obrigatório"));

        verify(categoriaService, never()).atualizarCategoria(eq(1L), any(CategoriaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoTentarAtualizarCategoriaComNomeVazio() throws Exception {

        String json = """
                {
                "nome" : ""
                }
                """;

        mockMvc.perform(put("/categorias/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("nome: Nome da categoria é obrigatório"));

        verify(categoriaService, never()).atualizarCategoria(eq(1L), any(CategoriaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoTentarAtualizarCategoriaComNomeSomenteComEspacos() throws Exception {

        String json = """
                {
                "nome" : "  "
                }
                """;

        mockMvc.perform(put("/categorias/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("nome: Nome da categoria é obrigatório"));

        verify(categoriaService, never()).atualizarCategoria(eq(1L), any(CategoriaRequestDTO.class));

    }

    @Test
    void deveRetornar204aoDeletarCategoriaComIdExistente() throws Exception{

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(categoriaService).deletarCategoria(1L);

    }

    @Test
    void deveRetornar404aoDeletarCategoriaComIdInexistente() throws Exception {

        doThrow(ResourceNotFoundException.class).when(categoriaService).deletarCategoria(99L);

        mockMvc.perform(delete("/categorias/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/categorias/99"));

        verify(categoriaService).deletarCategoria(99L);

    }

    @Test
    void deveRetornar409aoTentarDeletarCategoriaComDespesaAssociada() throws Exception {

        doThrow(DataBaseException.class).when(categoriaService).deletarCategoria(99L);

        mockMvc.perform(delete("/categorias/{id}", 99L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Erro no banco de dados"))
                .andExpect(jsonPath("$.path").value("/categorias/99"));

        verify(categoriaService).deletarCategoria(99L);

    }

}
