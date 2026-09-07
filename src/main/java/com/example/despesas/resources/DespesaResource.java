package com.example.despesas.resources;

import com.example.despesas.entities.Despesa;
import com.example.despesas.services.DespesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<Despesa> cadastrarDespesa(@Valid @RequestBody Despesa obj) {
        obj = service.cadastrarDespesa(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Despesa> atualizarDespesa(@PathVariable Long id,@Valid @RequestBody Despesa obj) {
        obj = service.atualizarDespesa(id, obj);
        return ResponseEntity.ok().body(obj);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarDespesa(@PathVariable Long id) {
        service.deletarDespesa(id);
        return ResponseEntity.noContent().build();
    }
}
