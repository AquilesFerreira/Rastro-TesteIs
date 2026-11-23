// A LIGAÇÃO COM O BACK-END.
// Uma única instância do axios com o endereço da API (vem do .env).
// Todos os services usam esta instância, então trocar o endereço da API é mexer só no .env.
import axios from 'axios'

export const api = axios.create({
    baseURL: import.meta.env?.VITE_API_URL || 'http://localhost:8080/rastro/api',
})
