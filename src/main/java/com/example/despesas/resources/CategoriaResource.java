package com.example.despesas.resources;

import com.example.despesas.dtos.CategoriaRequestDTO;
import com.example.despesas.dtos.CategoriaResponseDTO;
import com.example.despesas.entities.Categoria;
import com.example.despesas.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaResource {

    private final CategoriaService service;

    public CategoriaResource(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<Categoria> list = service.listarCategorias();
        List<CategoriaResponseDTO> categoriasDtos = list.stream()
                .map(categoria -> new CategoriaResponseDTO(categoria)).toList();
        return ResponseEntity.ok().body(categoriasDtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = service.buscarCategoriaPorId(id);
        return ResponseEntity.ok().body(new CategoriaResponseDTO(categoria));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> cadastrarCategoria(@Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria categoria = service.cadastrarCategoria(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(uri).body(new CategoriaResponseDTO(categoria));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria categoria = service.atualizarCategoria(id, dto);
        return ResponseEntity.ok().body(new CategoriaResponseDTO(categoria));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        service.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }

}
