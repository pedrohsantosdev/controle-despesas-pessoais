package com.example.despesas.resources;

import com.example.despesas.dtos.DespesaRequestDTO;
import com.example.despesas.dtos.DespesaUpdateDTO;
import com.example.despesas.dtos.ResumoDespesasDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.entities.Despesa;
import com.example.despesas.services.DespesaService;
import com.example.despesas.services.exceptions.InvalidDate;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DespesaResource.class)
@ActiveProfiles("test")
public class DespesaResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DespesaService despesaService;

    @Test
    void deveRetornar200aoListarTodasAsDespesasCadastradas() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 27), true
        );

        when(despesaService.listarDespesas(null, null)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(despesaService).listarDespesas(null, null);

    }

    @Test
    void deveRetornar200aoListarDespesasPorFiltroDeCategoriaIdEcampoPagamentoConfirmado() throws Exception {

        Categoria categoria = new Categoria(1L, "Contas redidencias");

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 27), true
        );

        d1.setCategoria(categoria);
        d2.setCategoria(categoria);

        when(despesaService.listarDespesas(1L, true)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas?categoriaId=1&paga=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("[0].paga").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].paga").value(true));

        verify(despesaService).listarDespesas(1L, true);

    }

    @Test
    void deveRetornar200aoListarDespesasPorFiltroDeCategoriaIdEpagamentoNaoRealizado() throws Exception {

        Categoria categoria = new Categoria(1L, "Contas redidencias");

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 27), false
        );

        d1.setCategoria(categoria);
        d2.setCategoria(categoria);

        when(despesaService.listarDespesas(1L, false)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas?categoriaId=1&paga=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].paga").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].paga").value(false));

        verify(despesaService).listarDespesas(1L, false);

    }

    @Test
    void deveRetornar200aoListarDespesasPorFiltroDePagamentoRealizado() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 27), true
        );

        when(despesaService.listarDespesas(null, true)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas?paga=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].paga").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].paga").value(true));

        verify(despesaService).listarDespesas(null, true);

    }

    @Test
    void deveRetornar200aoListarDespesasPorFiltroDePagamentoNaoRealizado() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 27), false
        );

        when(despesaService.listarDespesas(null, false)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas?paga=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("[0].paga").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].paga").value(false));

        verify(despesaService).listarDespesas(null, false);
    }

    @Test
    void deveRetornar200aoListarDespesasPorFiltroDeCategoriaId() throws Exception {

        Categoria categoria = new Categoria(1L, "Contas residenciais");

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 27), true
        );

        d1.setCategoria(categoria);
        d2.setCategoria(categoria);

        when(despesaService.listarDespesas(1L, null)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas?categoriaId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(despesaService).listarDespesas(1L, null);

    }

    @Test
    void deveRetornar200comListarVaziaDeTodasAsDespesasCadastradas() throws Exception {

        when(despesaService.listarDespesas(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/despesas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(despesaService).listarDespesas(null, null);
    }

    @Test
    void deveRetornar200aoBuscarDespesaComIdExistente() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        when(despesaService.buscarDespesaPorId(1L)).thenReturn(d1);

        mockMvc.perform(get("/despesas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.descricao").value("Conta de internet"))
                .andExpect(jsonPath("$.valor").value(120.00))
                .andExpect(jsonPath("$.dataVencimento").value("2026-10-25"))
                .andExpect(jsonPath("$.paga").value(true));

        verify(despesaService).buscarDespesaPorId(1L);

    }

    @Test
    void deveRetornar404aoBuscarDespesaComIdInexistente() throws Exception {

        when(despesaService.buscarDespesaPorId(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(get("/despesas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/despesas/99"));

        verify(despesaService).buscarDespesaPorId(99L);

    }

    @Test
    void deveRetornar200aoListarDespesasVencidas() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 02), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 03), false
        );

        when(despesaService.listarDespesasVencidas()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas/vencidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].paga").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].paga").value(false));

        verify(despesaService).listarDespesasVencidas();

    }

    @Test
    void deveRetornar200aoListarListaVaziaDeDespesasVencidas() throws Exception {

        when(despesaService.listarDespesasVencidas()).thenReturn(List.of());

        mockMvc.perform(get("/despesas/vencidas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(despesaService).listarDespesasVencidas();

    }

    @Test
    void deveRetornar200aoListarDespesasPorPeriodoComDataValida() throws Exception {

        LocalDate inicio = LocalDate.of(2026, 10, 01);
        LocalDate fim = LocalDate.of(2026, 10, 31);

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 02), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 150.00,
                LocalDate.of(2026, 10, 03), true
        );

        when(despesaService.listarDespesasPorPeriodo(inicio, fim)).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/despesas/periodo?inicio=2026-10-01&fim=2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(despesaService).listarDespesasPorPeriodo(inicio, fim);

    }

    @Test
    void deveRetornar400aoListarDespesasPorPeriodoComAdataInicialPosteriorAfinal() throws Exception {

        LocalDate inicio = LocalDate.of(2026, 10, 31);
        LocalDate fim = LocalDate.of(2026, 10, 01);

        when(despesaService.listarDespesasPorPeriodo(inicio, fim)).thenThrow(new InvalidDate("Erro na data"));

        mockMvc.perform(get("/despesas/periodo?inicio=2026-10-31&fim=2026-10-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data inválida"))
                .andExpect(jsonPath("$.path")
                        .value("/despesas/periodo"));

        verify(despesaService).listarDespesasPorPeriodo(inicio, fim);

    }

    @Test
    void deveRetornar200aoResumirDespesasPorPeriodo() throws Exception {

        LocalDate inicio = LocalDate.of(2026, 10, 01);
        LocalDate fim = LocalDate.of(2026, 10, 31);

        ResumoDespesasDTO dto = new ResumoDespesasDTO(270.00, 120.00, 150.00);

        when(despesaService.resumirDespesasPorPeriodo(inicio, fim)).thenReturn(dto);

        mockMvc.perform(get("/despesas/resumo?inicio=2026-10-01&fim=2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total"). value(270.00))
                .andExpect(jsonPath("$.totalPago").value(120.00))
                .andExpect(jsonPath("$.totalPendente").value(150.00));

        verify(despesaService).resumirDespesasPorPeriodo(inicio, fim);

    }

    @Test
    void deveRetornar400aoTentarResumirDespesasPorPeriodoComDataInicialPosteriorAdataFinal() throws Exception {

        LocalDate inicio = LocalDate.of(2026, 10, 31);
        LocalDate fim = LocalDate.of(2026, 10, 01);

        when(despesaService.resumirDespesasPorPeriodo(inicio, fim)).thenThrow(new InvalidDate("Erro na data"));

        mockMvc.perform(get("/despesas/resumo?inicio=2026-10-31&fim=2026-10-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data inválida"))
                .andExpect(jsonPath("$.path")
                        .value("/despesas/resumo"));

        verify(despesaService).resumirDespesasPorPeriodo(inicio, fim);

    }

    @Test
    void deveRetornar201aoCadastrarDespesaSemCategoriaComDadosValidos() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        when(despesaService.cadastrarDespesa(any(DespesaRequestDTO.class))).thenReturn(d1);

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/despesas/1"))
                .andExpect(jsonPath("$.id").value(1L));

        ArgumentCaptor<DespesaRequestDTO> captor = ArgumentCaptor.forClass(DespesaRequestDTO.class);
        verify(despesaService).cadastrarDespesa(captor.capture());
        DespesaRequestDTO dto = captor.getValue();

        assertEquals(d1.getDescricao(), dto.descricao());
        assertEquals(d1.getValor(), dto.valor());
        assertEquals(d1.getDataVencimento(), dto.dataVencimento());
        assertTrue(dto.paga());
        assertNull(dto.categoriaId());

        verify(despesaService).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar201aoCadastrarDespesaComCategoriaEdadosValidos() throws Exception {

        Categoria categoria = new Categoria(1L, "Contas residenciais");

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        d1.setCategoria(categoria);

        when(despesaService.cadastrarDespesa(any(DespesaRequestDTO.class))).thenReturn(d1);

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": 1
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/despesas/1"))
                .andExpect(jsonPath("$.id").value(1L));

        ArgumentCaptor<DespesaRequestDTO> captor = ArgumentCaptor.forClass(DespesaRequestDTO.class);
        verify(despesaService).cadastrarDespesa(captor.capture());
        DespesaRequestDTO dto = captor.getValue();

        assertEquals(d1.getDescricao(), dto.descricao());
        assertEquals(d1.getValor(), dto.valor());
        assertEquals(d1.getDataVencimento(), dto.dataVencimento());
        assertTrue(dto.paga());
        assertEquals(d1.getCategoria().getId(), dto.categoriaId());

        verify(despesaService).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComDescricaoNula() throws Exception {

        String json = """
                {
                "descricao" : null,
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("descricao: Descrição é obrigatória"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComDescricaoVazia() throws Exception {

        String json = """
                {
                "descricao" : "",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("descricao: Descrição é obrigatória"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComDescricaoSomenteComEspacos() throws Exception {

        String json = """
                {
                "descricao" : "  ",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("descricao: Descrição é obrigatória"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComValorNulo() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : null,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor da conta é obrigatório"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComValorVazio() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : "",
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor da conta é obrigatório"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComValorSomenteComEspacos() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : "  ",
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor da conta é obrigatório"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComValorNegativo() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : -1,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor deve ser maior que zero"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComValorIgualAzero() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 0.0,
                "dataVencimento" : "2026-10-25",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor deve ser maior que zero"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComDataDeVencimentoNula() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : null,
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("dataVencimento: A data de vencimento é obrigatória"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComDataDeVencimentoVazia() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("dataVencimento: A data de vencimento é obrigatória"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar400aoCadastrarDespesaComDataDeVencimentoSomenteComEspacos() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "  ",
                "paga": true,
                "categoriaId": null
                }
                """;

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("dataVencimento: A data de vencimento é obrigatória"));

        verify(despesaService, never()).cadastrarDespesa(any(DespesaRequestDTO.class));

    }

    @Test
    void deveRetornar200aoAtualizarDespesaComDadosValidosEidExistente() throws Exception {

        Categoria categoria = new Categoria(1L, "Contas residenciais");

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        d1.setCategoria(categoria);

        when(despesaService.atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class))).thenReturn(d1);

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "categoriaId" : 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        ArgumentCaptor<DespesaUpdateDTO> captor = ArgumentCaptor.forClass(DespesaUpdateDTO.class);
        verify(despesaService).atualizarDespesa(eq(1L), captor.capture());
        DespesaUpdateDTO dto = captor.getValue();

        assertEquals("Conta de internet", dto.descricao());
        assertEquals(120.00, dto.valor());
        assertEquals(LocalDate.of(2026, 10, 25), dto.dataVencimento());
        assertEquals(1L, dto.categoriaId());

    }

    @Test
    void deveRetornar404aoTentarAtualizarDespesaComIdInexistente() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "categoriaId" : 1
                }
                """;

        when(despesaService.atualizarDespesa(eq(99L), any(DespesaUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(put("/despesas/{id}", 99L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/despesas/99"));

        verify(despesaService).atualizarDespesa(eq(99L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar404aoTentarAtualizarDespesaComUmaCategoriaComIdInexistente() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "categoriaId" : 99
                }
                """;

        when(despesaService.atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class))).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/despesas/1"));

        verify(despesaService).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComDescricaoNula() throws Exception {

        String json = """
                {
                "descricao" : null,
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("descricao: Descrição é obrigatória"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComDescricaoVazia() throws Exception {

        String json = """
                {
                "descricao" : "",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("descricao: Descrição é obrigatória"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComDescricaoSomenteComEspacos() throws Exception {

        String json = """
                {
                "descricao" : "  ",
                "valor" : 120.00,
                "dataVencimento" : "2026-10-25",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("descricao: Descrição é obrigatória"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComValorNulo() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : null,
                "dataVencimento" : "2026-10-25",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor da conta é obrigatório"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComValorNegativo() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : -1,
                "dataVencimento" : "2026-10-25",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor deve ser maior que zero"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComValorIgualAzero() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 0,
                "dataVencimento" : "2026-10-25",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("valor: O valor deve ser maior que zero"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComDataDeVencimentoNula() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : null,
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("dataVencimento: A data de vencimento é obrigatória"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComDataDeVencimentoVazia() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("dataVencimento: A data de vencimento é obrigatória"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar400aoAtualizarDespesaComDataDeVencimentoSomenteComEspacos() throws Exception {

        String json = """
                {
                "descricao" : "Conta de internet",
                "valor" : 120.00,
                "dataVencimento" : "  ",
                "categoriaId": 1
                }
                """;

        mockMvc.perform(put("/despesas/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro na validação"))
                .andExpect(jsonPath("$.message").value("dataVencimento: A data de vencimento é obrigatória"));

        verify(despesaService, never()).atualizarDespesa(eq(1L), any(DespesaUpdateDTO.class));

    }

    @Test
    void deveRetornar204aoDeletarUmaDespesaComIdValido() throws Exception {

        mockMvc.perform(delete("/despesas/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(despesaService).deletarDespesa(1L);
    }

    @Test
    void deveRetornar404aoTentarDeletarDespesaComIdInexistente() throws Exception {

        doThrow(new ResourceNotFoundException(99L)).when(despesaService).deletarDespesa(99L);

        mockMvc.perform(delete("/despesas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/despesas/99"));

        verify(despesaService).deletarDespesa(99L);
    }

    @Test
    void deveRetornar200aoMarcarDespesaComoPaga() throws Exception {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 25), true
        );

        when(despesaService.marcarDespesaComoPaga(1L)).thenReturn(d1);

        mockMvc.perform(patch("/despesas/{id}/pagar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.paga").value(true));

        verify(despesaService).marcarDespesaComoPaga(1L);
    }

    @Test
    void deveRetornar404aoTentarMarcarComoPagaUmaDespesComIdInexistente() throws Exception {

        when(despesaService.marcarDespesaComoPaga(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(patch("/despesas/{id}/pagar", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/despesas/99/pagar"));

        verify(despesaService).marcarDespesaComoPaga(99L);
    }

}
