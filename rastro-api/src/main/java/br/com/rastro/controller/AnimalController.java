package br.com.rastro.controller;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import br.com.rastro.dto.animal.AnimalResponseDTO;
import br.com.rastro.dto.animal.BaixaRequestDTO;
import br.com.rastro.entity.Animal;
import br.com.rastro.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Animais: consulta, edição básica e baixa.
 * Cadastro de animal é feito pelo lote de compra; venda, pelo lote de venda.
 */
@RestController
@RequestMapping("/rastro/api/animais")
public class AnimalController {

    @Autowired
    private AnimalService service;

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.findAll().stream().map(AnimalController::toResponseDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(service.findById(id)));
    }

    @GetMapping("/brinco/{numero}")
    public ResponseEntity<AnimalResponseDTO> buscarPorNumeroBrinco(@PathVariable Integer numero) {
        return ResponseEntity.ok(toResponseDTO(service.findByNumeroBrinco(numero)));
    }

    /** Corrige brinco, sexo, castrado e raça. Não muda lote nem status. */
    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody AnimalRequestDTO dto) {
        return ResponseEntity.ok(toResponseDTO(service.update(id, dto)));
    }

    @PatchMapping("/{id}/baixa")
    public ResponseEntity<AnimalResponseDTO> darBaixa(@PathVariable Long id,
                                                      @Valid @RequestBody BaixaRequestDTO dto) {
        return ResponseEntity.ok(toResponseDTO(service.darBaixa(id, dto)));
    }

    // static: o LoteCompraController também usa para devolver os animais do lote
    static AnimalResponseDTO toResponseDTO(Animal a) {
        return new AnimalResponseDTO(
                a.getId(), a.getNumeroBrinco(), a.getSexo(), a.getCastrado(),
                a.getRaca(), a.getLoteCompra().getId(),
                a.getLoteVenda() != null ? a.getLoteVenda().getId() : null,
                a.getStatus().name(), a.getMotivoBaixa(), a.getDataBaixa()
        );
    }
}
