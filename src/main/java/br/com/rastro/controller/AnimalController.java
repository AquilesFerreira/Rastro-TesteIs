package br.com.rastro.controller;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import br.com.rastro.dto.animal.AnimalResponseDTO;
import br.com.rastro.entity.Animal;
import br.com.rastro.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rastro/api/animais")
public class AnimalController {

    @Autowired
    private AnimalService service;

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listarTodos() {
        List<AnimalResponseDTO> response = service.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(service.findById(id)));
    }

    @GetMapping("/brinco/{numero}")
    public ResponseEntity<AnimalResponseDTO> buscarPorNumeroBrinco(@PathVariable Integer numero) {
        return ResponseEntity.ok(toResponseDTO(service.findByNumeroBrinco(numero)));
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> criar(@Valid @RequestBody AnimalRequestDTO dto) {
        Animal salvo = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnimalRequestDTO dto
    ) {
        Animal atualizado = service.update(id, dto);
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AnimalResponseDTO toResponseDTO(Animal a) {
        return new AnimalResponseDTO(
                a.getId(), a.getNumeroBrinco(), a.getSexo(), a.getCastrado(),
                a.getRaca(), a.getLoteCompra().getId(),
                a.getLoteVenda() != null ? a.getLoteVenda().getId() : null
        );
    }
}