package com.example.despesas.resources;

import com.example.despesas.entities.Despesa;
import com.example.despesas.services.DespesaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/despesas")
public class DespesaResource {

    private final DespesaService service;

    public DespesaResource(DespesaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Despesa>> listarDespesas() {
        List<Despesa> list = service.listarDespesas();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Despesa> buscarDespesaPorId(@PathVariable Long id) {
        Despesa obj = service.buscarDespesaPorId(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<Despesa> cadastrarDespesa(@RequestBody Despesa obj) {
        obj = service.cadastrarDespesa(obj);
        return ResponseEntity.status(HttpStatus.CREATED).body(obj);
    }
}
