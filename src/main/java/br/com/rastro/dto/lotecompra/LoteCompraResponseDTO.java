package br.com.rastro.dto.lotecompra;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteCompraResponseDTO(
        Long id,
        LocalDate data,
        String fornecedor,
        Integer quantidade,
        BigDecimal pesoTotal,
        BigDecimal valorTotal
) {
}
