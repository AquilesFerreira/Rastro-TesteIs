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
        return ResponseEntity.ok(service.findAll().stream().map(this::toResponseDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteVendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(service.findById(id)));
    }

    /** Cria a venda com os animais selecionados (campo "animaisIds"). */
    @PostMapping
    public ResponseEntity<LoteVendaResponseDTO> criar(@Valid @RequestBody LoteVendaRequestDTO dto) {
        LoteVenda salvo = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteVendaResponseDTO> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody LoteVendaRequestDTO dto) {
        return ResponseEntity.ok(toResponseDTO(service.update(id, dto)));
    }

    /** Exclui a venda; os animais voltam a ficar ATIVOS. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LoteVendaResponseDTO toResponseDTO(LoteVenda l) {
        return new LoteVendaResponseDTO(
                l.getId(), l.getData(), l.getComprador(),
                l.getQuantidade(), l.getPesoTotal(), l.getValorTotal(),
                service.idsDosAnimais(l.getId())
        );
    }
}
