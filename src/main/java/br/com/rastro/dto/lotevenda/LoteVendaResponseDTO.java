package br.com.rastro.dto.lotevenda;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteVendaResponseDTO(
        Long id,
        LocalDate data,
        String comprador,
        Integer quantidade,
        BigDecimal pesoTotal,
        BigDecimal valorTotal
) {}
