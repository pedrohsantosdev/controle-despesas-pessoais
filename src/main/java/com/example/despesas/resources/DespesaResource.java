package com.example.despesas.resources;

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
    public ResponseEntity<List<Despesa>> listarDespesas(@RequestParam(name = "paga", required = false) Boolean paga) {
        List<Despesa> list = service.listarDespesas(paga);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Despesa> buscarDespesaPorId(@PathVariable Long id) {
        Despesa obj = service.buscarDespesaPorId(id);
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping(value = "/vencidas")
    public ResponseEntity<List<Despesa>> listarDespesasVencidas() {
        List<Despesa> list = service.listarDespesasVencidas();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/periodo")
    public ResponseEntity<List<Despesa>> listarDespesaPorPeriodo(@RequestParam(name = "inicio")
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate inicio,
                                                                 @RequestParam(name = "fim")
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate fim) {
        List<Despesa> list = service.listarDespesasPorPeriodo(inicio, fim);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<Despesa> cadastrarDespesa(@Valid @RequestBody Despesa obj) {
        obj = service.cadastrarDespesa(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Despesa> atualizarDespesa(@PathVariable Long id, @Valid @RequestBody Despesa obj) {
        obj = service.atualizarDespesa(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarDespesa(@PathVariable Long id) {
        service.deletarDespesa(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/pagar")
    public ResponseEntity<Despesa> marcarDespesaComoPaga(@PathVariable Long id) {
        Despesa obj = service.marcarDespesaComoPaga(id);
        return ResponseEntity.ok().body(obj);
    }
}
