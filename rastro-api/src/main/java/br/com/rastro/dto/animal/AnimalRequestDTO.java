package br.com.rastro.dto.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Dados de UM animal, usados dentro do lote de compra.
 * Não tem loteCompraId: o lote é quem cria o animal.
 * Não tem loteVendaId: a venda é feita pelo lote de venda.
 */
public record AnimalRequestDTO(
        @NotNull(message = "Número do brinco não pode ser nulo")
        Integer numeroBrinco,

        @NotBlank(message = "Sexo não pode ser vazio")
        @Pattern(regexp = "[MF]", message = "Sexo deve ser M ou F")
        String sexo,

        @NotBlank(message = "Castrado não pode ser vazio")
        @Pattern(regexp = "[SN]", message = "Castrado deve ser S ou N")
        String castrado,

        @NotBlank(message = "Raça não pode ser vazia")
        String raca
) {}
