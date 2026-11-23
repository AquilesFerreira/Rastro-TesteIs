// Uma função para cada endpoint do LoteCompraController (/rastro/api/lotes-compra).
// O front nunca fala com o banco: ele chama estas URLs e a API faz o resto.
import { api } from './api.js'

export async function listarLotesCompra() {
    const response = await api.get('/lotes-compra')            // GET - findAll()
    return response.data
}

export async function buscarLoteCompra(id) {
    const response = await api.get(`/lotes-compra/${id}`)       // GET - findById()
    return response.data
}

// O lote vai já com a lista de animais: { data, fornecedor, pesoTotal, valorTotal, animais: [...] }
// A API cria o lote e todos os animais numa transação só.
export async function cadastrarLoteCompra(lote) {
    const response = await api.post('/lotes-compra', lote)      // POST - save()
    return response.data
}

// Edita só o cabeçalho (data, fornecedor, peso, valor).
export async function atualizarLoteCompra(id, lote) {
    const response = await api.put(`/lotes-compra/${id}`, lote) // PUT - update()
    return response.data
}

// Exclui a compra e os animais dela (a API bloqueia se algum foi vendido ou baixado).
export async function excluirLoteCompra(id) {
    await api.delete(`/lotes-compra/${id}`)                     // DELETE - delete()
}

// Animais dentro do lote 

export async function listarAnimaisDoLote(id) {
    const response = await api.get(`/lotes-compra/${id}/animais`)
    return response.data
}

// Animal esquecido no cadastro: entra no lote e a quantidade sobe.
export async function adicionarAnimalAoLote(id, animal) {
    const response = await api.post(`/lotes-compra/${id}/animais`, animal)
    return response.data
}

// Animal cadastrado por engano (ex.: brinco errado): sai do lote e a quantidade desce.
export async function removerAnimalDoLote(id, animalId) {
    await api.delete(`/lotes-compra/${id}/animais/${animalId}`)
}
