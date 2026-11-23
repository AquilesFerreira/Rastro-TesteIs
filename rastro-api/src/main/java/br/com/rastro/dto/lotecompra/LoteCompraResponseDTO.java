package br.com.rastro.dto.lotecompra;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * quantidade = total de animais que entraram no lote.
 * ativos / vendidos / baixas = como esses animais estão hoje.
 */
public record LoteCompraResponseDTO(
        Long id,
        LocalDate data,
        String fornecedor,
        Integer quantidade,
        BigDecimal pesoTotal,
        BigDecimal valorTotal,
        long ativos,
        long vendidos,
        long baixas
) {
}
