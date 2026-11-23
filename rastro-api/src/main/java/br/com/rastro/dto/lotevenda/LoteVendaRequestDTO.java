package br.com.rastro.dto.lotevenda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Venda: o lote é montado selecionando animais ATIVOS pelo id.
 * A quantidade é calculada (= tamanho da lista).
 */
public record LoteVendaRequestDTO(
        @NotNull(message = "Data não pode ser nula")
        LocalDate data,

        @NotBlank(message = "Comprador não pode ser vazio")
        String comprador,

        @NotNull(message = "Peso total não pode ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "Peso deve ser maior que zero")
        BigDecimal pesoTotal,

        @NotNull(message = "Valor total não pode ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
        BigDecimal valorTotal,

        @NotEmpty(message = "Selecione pelo menos um animal")
        List<Long> animaisIds
) {}
