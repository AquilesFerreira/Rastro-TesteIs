package br.com.rastro.dto.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/** Corpo do PATCH /animais/{id}/baixa. Data é opcional (padrão: hoje). */
public record BaixaRequestDTO(
        @NotBlank(message = "Motivo não pode ser vazio")
        @Pattern(regexp = "MORTE|DESCARTE|OUTRO", message = "Motivo deve ser MORTE, DESCARTE ou OUTRO")
        String motivo,

        LocalDate data
) {}
