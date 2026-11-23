package br.com.rastro.entity;

/**
 * Situação do animal no rebanho.
 * ATIVO   -> está na propriedade (entrou por um lote de compra)
 * VENDIDO -> saiu num lote de venda
 * BAIXA   -> saiu por morte, descarte ou outro motivo
 */
public enum StatusAnimal {
    ATIVO, VENDIDO, BAIXA
}
