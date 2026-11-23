package br.com.rastro.controller;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import br.com.rastro.dto.animal.AnimalResponseDTO;
import br.com.rastro.dto.lotecompra.LoteCompraRequestDTO;
import br.com.rastro.dto.lotecompra.LoteCompraResponseDTO;
import br.com.rastro.entity.LoteCompra;
import br.com.rastro.entity.StatusAnimal;
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
        return ResponseEntity.ok(service.findAll().stream().map(this::toResponseDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteCompraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(service.findById(id)));
    }

    /** Cria o lote JÁ com os animais (campo "animais" no corpo). */
    @PostMapping
    public ResponseEntity<LoteCompraResponseDTO> criar(@Valid @RequestBody LoteCompraRequestDTO dto) {
        LoteCompra salvo = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    /** Altera só o cabeçalho do lote. */
    @PutMapping("/{id}")
    public ResponseEntity<LoteCompraResponseDTO> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody LoteCompraRequestDTO dto) {
        return ResponseEntity.ok(toResponseDTO(service.update(id, dto)));
    }

    /** Exclui a compra e os animais dela (só se nenhum foi vendido ou baixado). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Animais do lote ----------

    @GetMapping("/{id}/animais")
    public ResponseEntity<List<AnimalResponseDTO>> listarAnimais(@PathVariable Long id) {
        return ResponseEntity.ok(service.animaisDoLote(id).stream().map(AnimalController::toResponseDTO).toList());
    }

    @PostMapping("/{id}/animais")
    public ResponseEntity<AnimalResponseDTO> adicionarAnimal(@PathVariable Long id,
                                                             @Valid @RequestBody AnimalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AnimalController.toResponseDTO(service.adicionarAnimal(id, dto)));
    }

    @DeleteMapping("/{id}/animais/{animalId}")
    public ResponseEntity<Void> removerAnimal(@PathVariable Long id, @PathVariable Long animalId) {
        service.removerAnimal(id, animalId);
        return ResponseEntity.noContent().build();
    }

    private LoteCompraResponseDTO toResponseDTO(LoteCompra l) {
        return new LoteCompraResponseDTO(
                l.getId(), l.getData(), l.getFornecedor(),
                l.getQuantidade(), l.getPesoTotal(), l.getValorTotal(),
                service.contar(l.getId(), StatusAnimal.ATIVO),
                service.contar(l.getId(), StatusAnimal.VENDIDO),
                service.contar(l.getId(), StatusAnimal.BAIXA)
        );
    }
}
