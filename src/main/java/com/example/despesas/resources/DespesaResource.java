package com.example.despesas.resources;

import com.example.despesas.dtos.DespesaRequestDTO;
import com.example.despesas.dtos.DespesaResponseDTO;
import com.example.despesas.dtos.DespesaUpdateDTO;
import com.example.despesas.dtos.ResumoDespesasDTO;
import com.example.despesas.entities.Despesa;
import com.example.despesas.services.DespesaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/despesas")
public class DespesaResource {

    private final DespesaService service;

    public DespesaResource(DespesaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DespesaResponseDTO>> listarDespesas(@RequestParam(name = "paga", required = false) Boolean paga,
                                                                   @RequestParam(name = "categoriaId", required = false) Long categoriaId) {

        List<Despesa> list = service.listarDespesas(paga, categoriaId);
        List<DespesaResponseDTO> despesasDto = list.stream()
                .map(despesa -> new DespesaResponseDTO(despesa)).toList();

        return ResponseEntity.ok().body(despesasDto);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DespesaResponseDTO> buscarDespesaPorId(@PathVariable Long id) {
        Despesa despesa = service.buscarDespesaPorId(id);
        return ResponseEntity.ok().body(new DespesaResponseDTO(despesa));
    }

    @GetMapping(value = "/vencidas")
    public ResponseEntity<List<DespesaResponseDTO>> listarDespesasVencidas() {
        List<Despesa> list = service.listarDespesasVencidas();
        List<DespesaResponseDTO> despesasDto = list.stream()
                .map(despesa -> new DespesaResponseDTO(despesa)).toList();

        return ResponseEntity.ok().body(despesasDto);
    }

    @GetMapping(value = "/periodo")
    public ResponseEntity<List<DespesaResponseDTO>> listarDespesaPorPeriodo(@RequestParam(name = "inicio")
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate inicio,
                                                                 @RequestParam(name = "fim")
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate fim) {

        List<Despesa> list = service.listarDespesasPorPeriodo(inicio, fim);
        List<DespesaResponseDTO> despesasDto = list.stream()
                        .map(despesa -> new DespesaResponseDTO(despesa)).toList();

        return ResponseEntity.ok().body(despesasDto);

    }

    @GetMapping(value = "/resumo")
    public ResponseEntity<ResumoDespesasDTO> resumirDespesasPorPeriodo(@RequestParam(name = "inicio")
                                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                       LocalDate inicio,
                                                                       @RequestParam(name = "fim")
                                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                       LocalDate fim) {

        ResumoDespesasDTO resumo = service.resumirDespesasPorPeriodo(inicio, fim);
        return ResponseEntity.ok().body(resumo);
    }

    @PostMapping
    public ResponseEntity<DespesaResponseDTO> cadastrarDespesa(@Valid @RequestBody DespesaRequestDTO dto) {
        Despesa despesa = service.cadastrarDespesa(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(despesa.getId()).toUri();
        return ResponseEntity.created(uri).body(new DespesaResponseDTO(despesa));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DespesaResponseDTO> atualizarDespesa(@PathVariable Long id, @Valid @RequestBody DespesaUpdateDTO dto) {
        Despesa despesa = service.atualizarDespesa(id, dto);
        return ResponseEntity.ok().body(new DespesaResponseDTO(despesa));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarDespesa(@PathVariable Long id) {
        service.deletarDespesa(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/pagar")
    public ResponseEntity<DespesaResponseDTO> marcarDespesaComoPaga(@PathVariable Long id) {
        Despesa despesa = service.marcarDespesaComoPaga(id);
        return ResponseEntity.ok().body(new DespesaResponseDTO(despesa));
    }
}
