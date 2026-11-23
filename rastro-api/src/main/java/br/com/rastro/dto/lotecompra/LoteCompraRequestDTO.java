package br.com.rastro.dto.lotecompra;

import br.com.rastro.dto.animal.AnimalRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Cadastro/edição do lote de compra.
 * Não existe mais o campo "quantidade": ela é calculada pelo número de animais.
 * - POST: "animais" é obrigatório (pelo menos 1) -> os animais nascem junto com o lote.
 * - PUT:  "animais" é ignorado -> só altera o cabeçalho (data, fornecedor, peso, valor).
 *         Para corrigir animais use POST/DELETE em /lotes-compra/{id}/animais.
 */
public record LoteCompraRequestDTO(
        @NotNull(message = "Data não pode ser nula")
        LocalDate data,

        @NotBlank(message = "Fornecedor não pode ser vazio")
        String fornecedor,

        @NotNull(message = "Peso total não pode ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "Peso deve ser maior que zero")
        BigDecimal pesoTotal,

        @NotNull(message = "Valor total não pode ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
        BigDecimal valorTotal,

        @Valid
        List<AnimalRequestDTO> animais
) {}
