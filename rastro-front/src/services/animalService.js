// Uma função para cada endpoint do AnimalController (/rastro/api/animais).
// Animal: CONSULTA, EDIÇÃO BÁSICA e BAIXA. Quem cria animal é o lote de compra,
// quem vende é o lote de venda.
import { api } from './api.js'

export async function listarAnimais() {
    const response = await api.get('/animais')                       // GET - findAll()
    return response.data
}

export async function buscarAnimal(id) {
    const response = await api.get(`/animais/${id}`)                  // GET - findById()
    return response.data
}

// Usa a consulta derivada findByNumeroBrinco do AnimalRepository.
export async function buscarAnimalPorBrinco(numero) {
    const response = await api.get(`/animais/brinco/${numero}`)
    return response.data
}

// Baixa por MORTE, DESCARTE ou OUTRO. O animal sai do rebanho ativo.
export async function darBaixaAnimal(id, motivo, data) {
    const response = await api.patch(`/animais/${id}/baixa`, { motivo, data: data || null })
    return response.data
}

// Edição básica: brinco, sexo, castrado e raça. Lote e status não mudam por aqui.
export async function atualizarAnimal(id, animal) {
    const response = await api.put(`/animais/${id}`, animal)
    return response.data
}
