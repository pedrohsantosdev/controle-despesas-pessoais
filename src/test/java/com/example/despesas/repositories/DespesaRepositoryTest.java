package com.example.despesas.repositories;

import com.example.despesas.entities.Categoria;
import com.example.despesas.entities.Despesa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
class DespesaRepositoryTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 14);

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void deveFiltrarPorPagamento() {
        Despesa paga = salvarDespesa(DATA, true, null);
        Despesa pendente = salvarDespesa(DATA, false, null);

        assertThat(despesaRepository.findByPaga(true)).containsExactly(paga);
        assertThat(despesaRepository.findByPaga(false)).containsExactly(pendente);
    }

    @Test
    void deveFiltrarPorCategoriaECombinarComPagamento() {
        Categoria casa = categoriaRepository.save(new Categoria(null, "Casa"));
        Categoria lazer = categoriaRepository.save(new Categoria(null, "Lazer"));
        Despesa paga = salvarDespesa(DATA, true, casa);
        Despesa pendente = salvarDespesa(DATA, false, casa);
        salvarDespesa(DATA, false, lazer);
        salvarDespesa(DATA, false, null);

        assertThat(despesaRepository.findByCategoria_Id(casa.getId()))
                .containsExactlyInAnyOrder(paga, pendente);
        assertThat(despesaRepository.findByCategoria_IdAndPaga(casa.getId(), false))
                .containsExactly(pendente);
        assertThat(despesaRepository.findByCategoria_IdAndPaga(casa.getId(), true))
                .containsExactly(paga);
    }

    @Test
    void deveConsiderarVencidasSomentePendentesAnterioresADataInformada() {
        Despesa vencida = salvarDespesa(DATA.minusDays(1), false, null);
        salvarDespesa(DATA.minusDays(1), true, null);
        salvarDespesa(DATA, false, null);
        salvarDespesa(DATA.plusDays(1), false, null);

        assertThat(despesaRepository.findByPagaFalseAndDataVencimentoBefore(DATA))
                .containsExactly(vencida);
    }

    @Test
    void deveIncluirOsDoisLimitesDoPeriodo() {
        LocalDate fim = DATA.plusDays(2);
        salvarDespesa(DATA.minusDays(1), false, null);
        Despesa inicio = salvarDespesa(DATA, false, null);
        Despesa meio = salvarDespesa(DATA.plusDays(1), true, null);
        Despesa ultima = salvarDespesa(fim, false, null);
        salvarDespesa(fim.plusDays(1), false, null);

        assertThat(despesaRepository.findByDataVencimentoBetween(DATA, fim))
                .containsExactlyInAnyOrder(inicio, meio, ultima);
        assertThat(despesaRepository.findByDataVencimentoBetween(DATA, DATA))
                .containsExactly(inicio);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverDespesasNoPeriodo() {
        salvarDespesa(DATA.minusDays(1), false, null);

        assertThat(despesaRepository.findByDataVencimentoBetween(DATA, DATA.plusDays(1)))
                .isEmpty();
    }

    @Test
    void deveDetectarVinculoApenasNaCategoriaComDespesas() {
        Categoria casa = categoriaRepository.save(new Categoria(null, "Casa"));
        Categoria lazer = categoriaRepository.save(new Categoria(null, "Lazer"));
        Despesa despesa = salvarDespesa(DATA, false, casa);

        assertThat(despesaRepository.existsByCategoria_Id(casa.getId())).isTrue();
        assertThat(despesaRepository.existsByCategoria_Id(lazer.getId())).isFalse();

        despesaRepository.delete(despesa);
        despesaRepository.flush();

        assertThat(despesaRepository.existsByCategoria_Id(casa.getId())).isFalse();
    }

    private Despesa salvarDespesa(LocalDate vencimento, boolean paga, Categoria categoria) {
        Despesa despesa = new Despesa(null, "Despesa de teste", 120.0, vencimento, paga);
        despesa.setCategoria(categoria);
        return despesaRepository.saveAndFlush(despesa);
    }
}
