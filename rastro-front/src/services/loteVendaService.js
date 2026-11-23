// Uma função para cada endpoint do LoteVendaController (/rastro/api/lotes-venda).
import { api } from './api.js'

export async function listarLotesVenda() {
    const response = await api.get('/lotes-venda')
    return response.data
}

export async function buscarLoteVenda(id) {
    const response = await api.get(`/lotes-venda/${id}`)
    return response.data
}

// A venda vai com os ids dos animais selecionados: { ..., animaisIds: [3, 7, 9] }
// A API marca esses animais como VENDIDO.
export async function cadastrarLoteVenda(lote) {
    const response = await api.post('/lotes-venda', lote)
    return response.data
}

export async function atualizarLoteVenda(id, lote) {
    const response = await api.put(`/lotes-venda/${id}`, lote)
    return response.data
}

// Exclui a venda: os animais voltam a ficar ATIVOS.
export async function excluirLoteVenda(id) {
    await api.delete(`/lotes-venda/${id}`)
}
