package br.com.rastro.dto.animal;

import java.time.LocalDate;

public record AnimalResponseDTO(
        Long id,
        Integer numeroBrinco,
        String sexo,
        String castrado,
        String raca,
        Long loteCompraId,
        Long loteVendaId,
        String status,
        String motivoBaixa,
        LocalDate dataBaixa
) { }
