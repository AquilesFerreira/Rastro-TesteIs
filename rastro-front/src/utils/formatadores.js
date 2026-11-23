// Formata os valores que chegam da API para o jeito brasileiro.

export function formatarMoeda(valor) {
    const numero = Number(valor)
    if (Number.isNaN(numero)) return 'R$ 0,00'
    return numero.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function formatarPeso(valor) {
    const numero = Number(valor)
    if (Number.isNaN(numero)) return '0 kg'
    return `${numero.toLocaleString('pt-BR', { maximumFractionDigits: 2 })} kg`
}

// A API manda LocalDate como "2026-07-10"; aqui vira "10/07/2026".
export function formatarData(dataIso) {
    if (!dataIso) return '—'
    const [ano, mes, dia] = dataIso.split('-')
    return `${dia}/${mes}/${ano}`
}

export function pesoMedio(pesoTotal, quantidade) {
    const q = Number(quantidade)
    if (!q) return 0
    return Number(pesoTotal) / q
}

// Brinco 7 → "BR-0007"
export function formatarBrinco(numero) {
    return `BR-${String(numero).padStart(4, '0')}`
}

// Rótulos dos status que vêm da API
export const STATUS_ANIMAL = {
    ATIVO: 'Ativo',
    VENDIDO: 'Vendido',
    BAIXA: 'Baixa',
}

export const MOTIVOS_BAIXA = {
    MORTE: 'Morte',
    DESCARTE: 'Descarte',
    OUTRO: 'Outro',
}
