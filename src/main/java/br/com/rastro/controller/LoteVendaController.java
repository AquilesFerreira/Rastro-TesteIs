package br.com.rastro.controller;

import br.com.rastro.dto.lotevenda.LoteVendaRequestDTO;
import br.com.rastro.dto.lotevenda.LoteVendaResponseDTO;
import br.com.rastro.entity.LoteVenda;
import br.com.rastro.service.LoteVendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rastro/api/lotes-venda")
public class LoteVendaController {

    @Autowired
    private LoteVendaService service;

    @GetMapping
    public ResponseEntity<List<LoteVendaResponseDTO>> listarTodos() {
        List<LoteVendaResponseDTO> response = service.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteVendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<LoteVendaResponseDTO> criar(@Valid @RequestBody LoteVendaRequestDTO dto) {
        LoteVenda salvo = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteVendaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LoteVendaRequestDTO dto
    ) {
        LoteVenda atualizado = service.update(id, dto);
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LoteVendaResponseDTO toResponseDTO(LoteVenda l) {
        return new LoteVendaResponseDTO(
                l.getId(), l.getData(), l.getComprador(),
                l.getQuantidade(), l.getPesoTotal(), l.getValorTotal()
        );
    }
}