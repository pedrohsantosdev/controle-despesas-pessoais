package com.example.despesas.integration;

import com.example.despesas.entities.Despesa;
import com.example.despesas.repositories.CategoriaRepository;
import com.example.despesas.repositories.DespesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DespesasApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void limparBanco() {
        despesaRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    void deveCadastrarConsultarPagarAtualizarEExcluirDespesa() throws Exception {
        long categoriaId = cadastrarCategoria();
        long despesaId = cadastrarDespesa(categoriaId);

        assertThat(despesaRepository.findById(despesaId).orElseThrow().getCategoria().getId())
                .isEqualTo(categoriaId);

        mockMvc.perform(get("/despesas/{id}", despesaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Internet"))
                .andExpect(jsonPath("$.paga").value(false));

        mockMvc.perform(patch("/despesas/{id}/pagar", despesaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paga").value(true));

        assertThat(despesaRepository.findById(despesaId).orElseThrow().isPaga()).isTrue();

        mockMvc.perform(put("/despesas/{id}", despesaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"Internet atualizada","valor":150.0,
                                 "dataVencimento":"2026-10-25","categoriaId":null}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paga").value(true));

        Despesa atualizada = despesaRepository.findById(despesaId).orElseThrow();
        assertThat(atualizada.getDescricao()).isEqualTo("Internet atualizada");
        assertThat(atualizada.getValor()).isEqualTo(150.0);
        assertThat(atualizada.getDataVencimento()).isEqualTo(LocalDate.of(2026, 10, 25));
        assertThat(atualizada.getCategoria()).isNull();
        assertThat(atualizada.isPaga()).isTrue();

        mockMvc.perform(delete("/despesas/{id}", despesaId))
                .andExpect(status().isNoContent());

        assertThat(despesaRepository.existsById(despesaId)).isFalse();
        mockMvc.perform(get("/despesas/{id}", despesaId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBloquearExclusaoDeCategoriaAteRemoverDespesaVinculada() throws Exception {
        long categoriaId = cadastrarCategoria();
        long despesaId = cadastrarDespesa(categoriaId);

        mockMvc.perform(delete("/categorias/{id}", categoriaId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.path").value("/categorias/" + categoriaId));

        assertThat(categoriaRepository.existsById(categoriaId)).isTrue();
        assertThat(despesaRepository.existsById(despesaId)).isTrue();

        mockMvc.perform(delete("/despesas/{id}", despesaId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/categorias/{id}", categoriaId))
                .andExpect(status().isNoContent());

        assertThat(categoriaRepository.existsById(categoriaId)).isFalse();
    }

    @Test
    void naoDevePersistirDespesaComCategoriaInexistente() throws Exception {
        mockMvc.perform(post("/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"Internet","valor":120.0,
                                 "dataVencimento":"2026-10-20","paga":false,"categoriaId":999999}
                                """))
                .andExpect(status().isNotFound());

        assertThat(despesaRepository.count()).isZero();
    }

    @Test
    void naoDevePersistirDespesaComValorInvalido() throws Exception {
        mockMvc.perform(post("/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"Internet","valor":0,
                                 "dataVencimento":"2026-10-20","paga":false}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/despesas"));

        assertThat(despesaRepository.count()).isZero();
    }

    @Test
    void deveResumirPeriodoIncluindoLimitesEIgnorandoDespesasForaDele() throws Exception {
        despesaRepository.save(new Despesa(null, "Paga", 120.0,
                LocalDate.of(2026, 10, 1), true));
        despesaRepository.save(new Despesa(null, "Pendente", 80.0,
                LocalDate.of(2026, 10, 31), false));
        despesaRepository.save(new Despesa(null, "Mes anterior", 500.0,
                LocalDate.of(2026, 9, 30), false));
        despesaRepository.save(new Despesa(null, "Mes seguinte", 600.0,
                LocalDate.of(2026, 11, 1), true));

        mockMvc.perform(get("/despesas/resumo")
                        .param("inicio", "2026-10-01").param("fim", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(200.0))
                .andExpect(jsonPath("$.totalPago").value(120.0))
                .andExpect(jsonPath("$.totalPendente").value(80.0));

        mockMvc.perform(get("/despesas/resumo")
                        .param("inicio", "2026-12-01").param("fim", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0.0))
                .andExpect(jsonPath("$.totalPago").value(0.0))
                .andExpect(jsonPath("$.totalPendente").value(0.0));
    }

    @Test
    void deveFiltrarDespesasDaCategoriaPeloPagamentoPersistido() throws Exception {
        long categoriaId = cadastrarCategoria();
        long pagaId = cadastrarDespesa(categoriaId);
        long pendenteId = cadastrarDespesa(categoriaId);
        long outraCategoriaId = cadastrarCategoria();
        cadastrarDespesa(outraCategoriaId);

        mockMvc.perform(patch("/despesas/{id}/pagar", pagaId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/despesas")
                        .param("categoriaId", Long.toString(categoriaId)).param("paga", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(pendenteId));
    }

    private long cadastrarCategoria() throws Exception {
        String location = mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Casa"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getHeader("Location");
        return extrairId(location);
    }

    private long cadastrarDespesa(long categoriaId) throws Exception {
        String location = mockMvc.perform(post("/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"Internet","valor":120.0,
                                 "dataVencimento":"2026-10-20","paga":false,"categoriaId":%d}
                                """.formatted(categoriaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getHeader("Location");
        long id = extrairId(location);
        assertThat(location).endsWith("/despesas/" + id);
        return id;
    }

    private long extrairId(String location) {
        assertThat(location).isNotNull();
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
