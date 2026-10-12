package com.example.despesas.services;

import com.example.despesas.dtos.DespesaRequestDTO;
import com.example.despesas.dtos.DespesaUpdateDTO;
import com.example.despesas.dtos.ResumoDespesasDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.entities.Despesa;
import com.example.despesas.repositories.DespesaRepository;
import com.example.despesas.services.exceptions.InvalidDate;
import com.example.despesas.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DespesaServiceTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private DespesaService despesaService;

    @Test
    void deveListarTodasAsDespesasQueNaoHouverFiltro() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        Despesa despesa1 = new Despesa(2L, "Conta de água", 85.00,
                LocalDate.of(2026, 10, 20), false
        );

        when(despesaRepository.findAll()).thenReturn(List.of(despesa, despesa1));

        List<Despesa> resultado = despesaService.listarDespesas(null, null);

        assertEquals(2, resultado.size());
        assertSame(despesa, resultado.get(0));
        assertSame(despesa1, resultado.get(1));
        verify(despesaRepository).findAll();

    }

    @Test
    void deveRetornarListaVazia() {

        when(despesaRepository.findAll()).thenReturn(List.of());

        List<Despesa> resultado = despesaService.listarDespesas(null, null);

        assertTrue(resultado.isEmpty());
        verify(despesaRepository).findAll();
    }

    @Test
    void deveListarDespesasNaoPagasPelaCategoria() {

        Categoria categoria = new Categoria(1L, "Despesas de casa");

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        Despesa despesa1 = new Despesa(2L, "Conta de água", 85.00,
                LocalDate.of(2026, 10, 20), false
        );

        despesa.setCategoria(categoria);
        despesa1.setCategoria(categoria);

        when(despesaRepository.findByCategoria_IdAndPaga(1L, false)).thenReturn(List.of(despesa, despesa1));

        List<Despesa> resultado = despesaService.listarDespesas(1L, false);

        assertEquals(2, resultado.size());
        assertSame(despesa, resultado.get(0));
        assertSame(despesa1, resultado.get(1));

        verify(despesaRepository).findByCategoria_IdAndPaga(1L, false);

    }

    @Test
    void deveListarDespesasPagasPelaCategoria() {

        Categoria categoria = new Categoria(1L, "Despesas de casa");

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), true
        );

        Despesa despesa1 = new Despesa(2L, "Conta de água", 85.00,
                LocalDate.of(2026, 10, 20), true
        );

        despesa.setCategoria(categoria);
        despesa1.setCategoria(categoria);

        when(despesaRepository.findByCategoria_IdAndPaga(1L, true)).thenReturn(List.of(despesa, despesa1));

        List<Despesa> resultado = despesaService.listarDespesas(1L, true);

        assertEquals(2, resultado.size());
        assertSame(despesa, resultado.get(0));
        assertSame(despesa1, resultado.get(1));

        verify(despesaRepository).findByCategoria_IdAndPaga(1L, true);

    }

    @Test
    void deveListarTodasAsDespesasPagas() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), true
        );

        Despesa despesa1 = new Despesa(2L, "Conta de água", 85.00,
                LocalDate.of(2026, 10, 20), true
        );

        when(despesaRepository.findByPaga(true)).thenReturn(List.of(despesa, despesa1));

        List<Despesa> resultado = despesaService.listarDespesas(null, true);

        assertEquals(2, resultado.size());
        assertSame(despesa, resultado.get(0));
        assertSame(despesa1, resultado.get(1));

        verify(despesaRepository).findByPaga(true);
    }

    @Test
    void deveListarTodasAsDespesasNaoPagas() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        Despesa despesa1 = new Despesa(2L, "Conta de água", 85.00,
                LocalDate.of(2026, 10, 20), false
        );

        when(despesaRepository.findByPaga(false)).thenReturn(List.of(despesa, despesa1));

        List<Despesa> resultado = despesaService.listarDespesas(null, false);

        assertEquals(2, resultado.size());
        assertSame(despesa, resultado.get(0));
        assertSame(despesa1, resultado.get(1));

        verify(despesaRepository).findByPaga(false);

    }

    @Test
    void deveListarTodasAsDespesasDeUmaCategoria() {

        Categoria categoria = new Categoria(1L, "Despesas de casa");

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), true
        );

        Despesa despesa1 = new Despesa(2L, "Conta de água", 85.00,
                LocalDate.of(2026, 10, 20), false
        );

        despesa.setCategoria(categoria);
        despesa1.setCategoria(categoria);

        when(despesaRepository.findByCategoria_Id(1L)).thenReturn(List.of(despesa, despesa1));

        List<Despesa> resultado = despesaService.listarDespesas(1L, null);

        assertEquals(2, resultado.size());
        assertSame(despesa, resultado.get(0));
        assertSame(despesa1, resultado.get(1));

        verify(despesaRepository).findByCategoria_Id(1L);

    }

    @Test
    void deveBuscarDespesaComIdExistente() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        Despesa resultado = despesaService.buscarDespesaPorId(1L);

        assertSame(despesa, resultado);
        verify(despesaRepository).findById(1L);

    }

    @Test
    void deveLancarExcecaoAoBuscarIdInexistente() {

        when(despesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> despesaService.buscarDespesaPorId(99L)
        );

        verify(despesaRepository).findById(99L);

    }

    @Test
    void deveCadastrarDespesaComCategoria() {

        Categoria categoria = new Categoria(1L, "Despesas de casa");

        DespesaRequestDTO dto = new DespesaRequestDTO("Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false, 1L
        );

        when(categoriaService.buscarCategoriaPorId(1L)).thenReturn(categoria);
        when(despesaRepository.save(any(Despesa.class))).thenAnswer(chamada -> chamada.getArgument(0));
        Despesa resultado = despesaService.cadastrarDespesa(dto);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());
        Despesa despesaCapturada = captor.getValue();

        assertSame(despesaCapturada, resultado);
        assertEquals("Conta de internet", despesaCapturada.getDescricao());
        assertEquals(120.00, despesaCapturada.getValor());
        assertEquals(LocalDate.of(2026, 10, 20), despesaCapturada.getDataVencimento());
        assertFalse(despesaCapturada.isPaga());
        assertSame(categoria, despesaCapturada.getCategoria());

    }

    @Test
    void deveCadastrarDespesasSemCategoria() {

        DespesaRequestDTO dto = new DespesaRequestDTO("Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false, null
        );

        when(despesaRepository.save(any(Despesa.class))).thenAnswer(chamada -> chamada.getArgument(0));
        Despesa resultado = despesaService.cadastrarDespesa(dto);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());
        Despesa despesaCapturada = captor.getValue();

        assertSame(despesaCapturada, resultado);
        assertEquals("Conta de internet", despesaCapturada.getDescricao());
        assertEquals(120.00, despesaCapturada.getValor());
        assertEquals(LocalDate.of(2026, 10, 20), despesaCapturada.getDataVencimento());
        assertFalse(despesaCapturada.isPaga());
        assertNull(despesaCapturada.getCategoria());
        verifyNoInteractions(categoriaService);

    }

    @Test
    void deveLancarExcecaoAoCadastrarComCategoriaInexistente() {

        DespesaRequestDTO dto = new DespesaRequestDTO("Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false, 99L
        );

        when(categoriaService.buscarCategoriaPorId(99L)).thenThrow(new ResourceNotFoundException(99L));

        assertThrows(
                ResourceNotFoundException.class,
                () -> despesaService.cadastrarDespesa(dto)
        );

        verify(categoriaService).buscarCategoriaPorId(99L);
        verify(despesaRepository, never()).save(any(Despesa.class));
        
    }

    @Test
    void deveAtualizarDespesaComIdExistente() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        DespesaUpdateDTO updateDTO = new DespesaUpdateDTO("Internet Residencial", 150.00,
                LocalDate.of(2026, 10, 28), null);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
        when(despesaRepository.save(any(Despesa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Despesa resultado = despesaService.atualizarDespesa(1L, updateDTO);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());
        Despesa despesaCapturada = captor.getValue();

        assertEquals(1L, despesaCapturada.getId());
        assertSame(despesa, despesaCapturada);
        assertSame(despesaCapturada, resultado);
        assertEquals("Internet Residencial", despesaCapturada.getDescricao());
        assertEquals(150.00, despesaCapturada.getValor());
        assertEquals(LocalDate.of(2026, 10, 28), despesaCapturada.getDataVencimento());
        assertFalse(despesaCapturada.isPaga());
        assertNull(despesaCapturada.getCategoria());
        verifyNoInteractions(categoriaService);

    }

    @Test
    void deveAtualizarDespesaParaUmaNovaCategoria() {

        Categoria categoriaAntiga = new Categoria(1L, "Outras despesas");
        Categoria categoria = new Categoria(2L, "Despesas de casa");

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        despesa.setCategoria(categoriaAntiga);

        DespesaUpdateDTO updateDTO = new DespesaUpdateDTO("Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), 2L);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
        when(categoriaService.buscarCategoriaPorId(2L)).thenReturn(categoria);
        when(despesaRepository.save(any(Despesa.class)))
                .thenAnswer(chamada -> chamada.getArgument(0));

        Despesa resultado = despesaService.atualizarDespesa(1L, updateDTO);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());
        Despesa despesaCapturada = captor.getValue();

        assertSame(despesa, resultado);
        assertSame(despesaCapturada, resultado);
        assertEquals(1L, despesaCapturada.getId());
        assertEquals("Conta de internet", despesaCapturada.getDescricao());
        assertEquals(120.00, despesaCapturada.getValor());
        assertEquals(LocalDate.of(2026, 10, 20), despesaCapturada.getDataVencimento());
        assertFalse(despesaCapturada.isPaga());
        assertSame(categoria, despesaCapturada.getCategoria());

        verify(categoriaService).buscarCategoriaPorId(2L);

    }

    @Test
    void deveLancarExcecaoAoAtualizarDespesaComIdInexistente() {

        DespesaUpdateDTO updateDTO = new DespesaUpdateDTO("Internet Residencial", 150.00,
                LocalDate.of(2026, 10, 20), null);

        when(despesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> despesaService.atualizarDespesa(99L, updateDTO)

        );

        verify(despesaRepository).findById(99L);
        verify(despesaRepository, never()).save(any(Despesa.class));
        verifyNoInteractions(categoriaService);

    }

    @Test
    void deveLancarExcecaoAoAtualizarDespesaComCategoriaInexistente() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        DespesaUpdateDTO updateDTO = new DespesaUpdateDTO("Internet Residencial", 150.00,
                LocalDate.of(2026, 10, 25), 99L);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
        when(categoriaService.buscarCategoriaPorId(99L)).thenThrow(new ResourceNotFoundException(99L));

        assertThrows(
                ResourceNotFoundException.class,
                () -> despesaService.atualizarDespesa(1L, updateDTO)
        );

        verify(categoriaService).buscarCategoriaPorId(99L);
        verify(despesaRepository, never()).save(any(Despesa.class));
        assertEquals("Conta de internet", despesa.getDescricao());
        assertEquals(120.00, despesa.getValor());
        assertEquals(LocalDate.of(2026, 10, 20), despesa.getDataVencimento());

    }

    @Test
    void deveRemoverCategoriaDeDespesa() {

        Categoria categoria = new Categoria(1L, "Contas de casa");

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        despesa.setCategoria(categoria);

        DespesaUpdateDTO updateDTO = new DespesaUpdateDTO("Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), null);

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
        when(despesaRepository.save(any(Despesa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Despesa resultado = despesaService.atualizarDespesa(1L, updateDTO);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());
        Despesa despesaCapturada = captor.getValue();

        assertSame(despesa, resultado);
        assertSame(despesaCapturada, resultado);
        assertEquals("Conta de internet", despesaCapturada.getDescricao());
        assertEquals(120.00, despesaCapturada.getValor());
        assertEquals(LocalDate.of(2026, 10, 20), despesaCapturada.getDataVencimento());
        assertNull(despesaCapturada.getCategoria());
        verifyNoInteractions(categoriaService);

    }

    @Test
    void deveManterDespesaPagaAoAtualizar() {

            Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                    LocalDate.of(2026, 10, 20), true
            );

            DespesaUpdateDTO updateDTO = new DespesaUpdateDTO("Internet Residencial", 150.00,
                    LocalDate.of(2026, 10, 20), null);

            when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));
            when(despesaRepository.save(any(Despesa.class))).thenAnswer(chamada -> chamada.getArgument(0));

            Despesa resultado = despesaService.atualizarDespesa(1L, updateDTO);

            ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
            verify(despesaRepository).save(captor.capture());
            Despesa despesaCapturada = captor.getValue();

            assertEquals(1L, despesaCapturada.getId());
            assertSame(despesa, despesaCapturada);
            assertSame(despesaCapturada, resultado);
            assertEquals("Internet Residencial", despesaCapturada.getDescricao());
            assertEquals(150.00, despesaCapturada.getValor());
            assertEquals(LocalDate.of(2026, 10, 20), despesaCapturada.getDataVencimento());
            assertTrue(despesaCapturada.isPaga());
            assertNull(despesaCapturada.getCategoria());
            verifyNoInteractions(categoriaService);

    }

    @Test
    void deveDeletarDespesaComIdExistente() {

        Despesa despesa = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 20), false
        );

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(despesa));

        despesaService.deletarDespesa(1L);

        verify(despesaRepository).delete(despesa);

    }

    @Test
    void deveLancarExcecaoAoDeletarDespesaComIdInexistente() {

        when(despesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> despesaService.deletarDespesa(99L)
        );

        verify(despesaRepository, never()).delete(any(Despesa.class));

    }

    @Test
    void deveListarDespesasVencidas() {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 07), false
        );

        Despesa d2 = new Despesa(2L, "Conta de Luz", 200.00,
                LocalDate.of(2026, 10, 05), false
        );

        when(despesaRepository.findByPagaFalseAndDataVencimentoBefore(LocalDate.now())).thenReturn(List.of(d1, d2));

        List<Despesa> resultado = despesaService.listarDespesasVencidas();

        assertEquals(2, resultado.size());
        assertSame(d1, resultado.get(0));
        assertSame(d2, resultado.get(1));

        verify(despesaRepository).findByPagaFalseAndDataVencimentoBefore(LocalDate.now());

    }

    @Test
    void deveMarcarDespesaComoPaga() {

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 15), false
        );

        when(despesaRepository.findById(1L)).thenReturn(Optional.of(d1));
        when(despesaRepository.save(any(Despesa.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Despesa resultado = despesaService.marcarDespesaComoPaga(1L);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).save(captor.capture());
        Despesa despesaCapturada = captor.getValue();

        assertSame(d1, despesaCapturada);
        assertSame(despesaCapturada, resultado);
        assertEquals(1L, despesaCapturada.getId());
        assertEquals("Conta de internet", despesaCapturada.getDescricao());
        assertEquals(120.00, despesaCapturada.getValor());
        assertEquals(LocalDate.of(2026, 10, 15), despesaCapturada.getDataVencimento());
        assertTrue(despesaCapturada.isPaga());

    }

    @Test
    void deveLancarExcecaoAoTentarMarcarDespesaComIdInexistenteComoPaga() {

        when(despesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> despesaService.marcarDespesaComoPaga(99L)
        );

        verify(despesaRepository, never()).save(any(Despesa.class));

    }

    @Test
    void deveListarDespesasPorPeriodo() {

        LocalDate inicio = LocalDate.of(2026, 10, 01);
        LocalDate fim = LocalDate.of(2026, 10, 30);

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 15), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 120.00,
                LocalDate.of(2026, 10, 17), false
        );

        when(despesaRepository.findByDataVencimentoBetween(inicio, fim)).thenReturn(List.of(d1, d2));

        List<Despesa> resultado = despesaService.listarDespesasPorPeriodo(inicio, fim);

        assertEquals(2, resultado.size());
        assertSame(d1, resultado.get(0));
        assertSame(d2, resultado.get(1));

        verify(despesaRepository).findByDataVencimentoBetween(inicio, fim);

    }

    @Test
    void deveListarDespesasPorPeriodoQuandoInicioEfimForemIguais() {

        LocalDate inicio = LocalDate.of(2026, 10, 01);
        LocalDate fim = LocalDate.of(2026, 10, 01);

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 01), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 120.00,
                LocalDate.of(2026, 10, 01), false
        );

        when(despesaRepository.findByDataVencimentoBetween(inicio, fim)).thenReturn(List.of(d1, d2));

        List<Despesa> resultado = despesaService.listarDespesasPorPeriodo(inicio, fim);

        assertEquals(2, resultado.size());
        assertSame(d1, resultado.get(0));
        assertSame(d2, resultado.get(1));

        verify(despesaRepository).findByDataVencimentoBetween(inicio, fim);

    }

    @Test
    void deveLancarExcecaoAoListarDespesasPorPeridoComDataInicialAposAfinal() {

        LocalDate inicio = LocalDate.of(2026, 10, 30);
        LocalDate fim = LocalDate.of(2026, 10, 01);

        assertThrows(
                InvalidDate.class,
                () -> despesaService.listarDespesasPorPeriodo(inicio, fim)
        );

        verify(despesaRepository, never()).findByDataVencimentoBetween(inicio, fim);

    }

    @Test
    void deveResumirDespesasPorPeriodo() {

        LocalDate inicio = LocalDate.of(2026, 10, 01);
        LocalDate fim = LocalDate.of(2026, 10, 30);

        Despesa d1 = new Despesa(1L, "Conta de internet", 120.00,
                LocalDate.of(2026, 10, 15), false
        );

        Despesa d2 = new Despesa(2L, "Conta de luz", 90.00,
                LocalDate.of(2026, 10, 17), false
        );

        Despesa d3 = new Despesa(3L, "Conta de água", 150.00,
                LocalDate.of(2026, 10, 17), true
        );

        when(despesaRepository.findByDataVencimentoBetween(inicio, fim)).thenReturn(List.of(d1, d2, d3));

        ResumoDespesasDTO dto = despesaService.resumirDespesasPorPeriodo(inicio, fim);

        assertEquals(210.00, dto.totalPendente());
        assertEquals(150.00, dto.totalPago());
        assertEquals(360.00, dto.total());

        verify(despesaRepository).findByDataVencimentoBetween(inicio, fim);

    }

    @Test
    void deveResumirPeriodoSemDespesas() {

        LocalDate inicio = LocalDate.of(2026, 10, 01);
        LocalDate fim = LocalDate.of(2026, 10, 30);

        when(despesaRepository.findByDataVencimentoBetween(inicio, fim)).thenReturn(List.of());

        ResumoDespesasDTO dto = despesaService.resumirDespesasPorPeriodo(inicio, fim);

        assertEquals(0.0, dto.totalPendente());
        assertEquals(0.0, dto.totalPago());
        assertEquals(0.0, dto.total());

        verify(despesaRepository).findByDataVencimentoBetween(inicio, fim);

    }

}
