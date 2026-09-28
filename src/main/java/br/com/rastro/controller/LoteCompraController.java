package br.com.rastro.controller;

import br.com.rastro.dto.lotecompra.LoteCompraRequestDTO;
import br.com.rastro.dto.lotecompra.LoteCompraResponseDTO;
import br.com.rastro.entity.LoteCompra;
import br.com.rastro.service.LoteCompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rastro/api/lotes-compra")
public class LoteCompraController {

    @Autowired
    private LoteCompraService service;

    @GetMapping
    public ResponseEntity<List<LoteCompraResponseDTO>> listarTodos() {
        List<LoteCompraResponseDTO> response = service.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteCompraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<LoteCompraResponseDTO> criar(@Valid @RequestBody LoteCompraRequestDTO dto) {
        LoteCompra salvo = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteCompraResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LoteCompraRequestDTO dto
    ) {
        LoteCompra atualizado = service.update(id, dto);
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LoteCompraResponseDTO toResponseDTO(LoteCompra l) {
        return new LoteCompraResponseDTO(
                l.getId(), l.getData(), l.getFornecedor(),
                l.getQuantidade(), l.getPesoTotal(), l.getValorTotal()
        );
    }
}