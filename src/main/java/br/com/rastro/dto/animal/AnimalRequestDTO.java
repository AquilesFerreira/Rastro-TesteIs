package br.com.rastro.dto.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnimalRequestDTO(
        @NotNull(message = "Número do brinco não pode ser nulo")
        Integer numeroBrinco,

        @NotBlank(message = "Sexo não pode ser vazio")
        String sexo,

        @NotBlank(message = "Castrado não pode ser vazio")
        String castrado,

        @NotBlank(message = "Raça não pode ser vazia")
        String raca,

        @NotNull(message = "Lote de compra não pode ser nulo")
        Long loteCompraId,

        Long loteVendaId
) {}
