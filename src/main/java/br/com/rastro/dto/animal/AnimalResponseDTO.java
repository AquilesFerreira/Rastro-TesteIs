package br.com.rastro.dto.animal;

public record AnimalResponseDTO(
        Long id,
        Integer numeroBrinco,
        String sexo,
        String castrado,
        String raca,
        Long loteCompraId,
        Long loteVendaId
) { }
