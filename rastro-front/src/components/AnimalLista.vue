<script setup>
// TELA de animais: CONSULTA, edição básica e baixa.
// O animal entra pelo lote de compra e sai pelo lote de venda (ou por baixa).
// Carrega também os lotes para mostrar o nome deles no card e filtrar por lote.
import { ref, computed, onMounted } from 'vue'
import { listarAnimais, buscarAnimalPorBrinco } from '../services/animalService.js'
import { listarLotesCompra } from '../services/loteCompraService.js'
import { listarLotesVenda } from '../services/loteVendaService.js'
import { useListagem } from '../composables/useListagem.js'
import { useFiltro } from '../composables/useFiltro.js'
import PainelLista from './PainelLista.vue'
import AnimalCard from './AnimalCard.vue'
import DarBaixa from './DarBaixa.vue'
import AnimalFormulario from './AnimalFormulario.vue'

const { itens: animais, carregando, erro, vazio, total, carregar } = useListagem(listarAnimais)
const { filtros, limpar } = useFiltro({ status: 'ATIVO', loteCompraId: '' })

const lotesCompra = ref([])
const lotesVenda = ref([])
async function carregarLotes() {
    try {
        const [compras, vendas] = await Promise.all([listarLotesCompra(), listarLotesVenda()])
        lotesCompra.value = compras
        lotesVenda.value = vendas
    } catch (e) {
        // Se os lotes não vierem, os cards mostram só o número do lote.
    }
}
const loteCompraPorId = computed(() => Object.fromEntries(lotesCompra.value.map(l => [l.id, l])))
const loteVendaPorId = computed(() => Object.fromEntries(lotesVenda.value.map(l => [l.id, l])))

// Filtros feitos no front, sobre a lista que veio da API.
const animaisVisiveis = computed(() => animais.value
    .filter(a => filtros.status === 'todos' || a.status === filtros.status)
    .filter(a => !filtros.loteCompraId || a.loteCompraId === Number(filtros.loteCompraId))
    .sort((a, b) => a.numeroBrinco - b.numeroBrinco))

const contagem = computed(() => {
    const c = { todos: animais.value.length, ATIVO: 0, VENDIDO: 0, BAIXA: 0 }
    animais.value.forEach(a => { c[a.status]++ })
    return c
})
const chipsStatus = [
    { valor: 'ATIVO', rotulo: 'Ativos' },
    { valor: 'VENDIDO', rotulo: 'Vendidos' },
    { valor: 'BAIXA', rotulo: 'Baixa' },
    { valor: 'todos', rotulo: 'Todos' },
]

// Busca pelo brinco: vai na API (GET /animais/brinco/{numero} → findByNumeroBrinco no banco).
const brincoBusca = ref('')
const erroBusca = ref('')
async function buscarPorBrinco() {
    erroBusca.value = ''
    if (!brincoBusca.value) return
    filtros.status = 'todos'
    filtros.loteCompraId = ''
    await carregar(async () => [await buscarAnimalPorBrinco(brincoBusca.value)])
    if (erro.value) {
        erro.value = ''
        erroBusca.value = `Nenhum animal com o brinco ${brincoBusca.value}.`
    }
}
function limparBusca() {
    brincoBusca.value = ''
    erroBusca.value = ''
    limpar()
    carregar()
}

// Edição básica
const animalEdicao = ref(null)
function aoEditar() { animalEdicao.value = null; carregar() }

// Baixa
const animalBaixa = ref(null)
function aoDarBaixa() { animalBaixa.value = null; atualizarTudo() }

function atualizarTudo() { carregar(); carregarLotes() }
onMounted(() => { atualizarTudo() })
</script>

<template>
    <div class="pagina-cabecalho">
        <div>
            <h1>Animais</h1>
            <p>Consulta do rebanho. O animal entra pelo lote de compra e sai pela venda ou por baixa.</p>
        </div>
    </div>

    <PainelLista titulo="Rebanho" :total="total" :carregando="carregando" :erro="erro"
        :vazio="vazio || (!carregando && !erro && animaisVisiveis.length === 0)"
        :mensagem-vazio="erroBusca || 'Nenhum animal nesse filtro.'" @atualizar="atualizarTudo">
        <template #filtros>
            <div class="filtros">
                <div class="opcoes">
                    <button v-for="c in chipsStatus" :key="c.valor" type="button" class="chip"
                        :class="{ ativo: filtros.status === c.valor }" @click="filtros.status = c.valor">
                        {{ c.rotulo }} ({{ contagem[c.valor] }})
                    </button>
                </div>
                <select v-model="filtros.loteCompraId">
                    <option value="">Todos os lotes de compra</option>
                    <option v-for="l in lotesCompra" :key="l.id" :value="l.id">#{{ l.id }} · {{ l.fornecedor }}</option>
                </select>
                <form class="busca-brinco" @submit.prevent="buscarPorBrinco">
                    <input v-model="brincoBusca" type="number" min="1" placeholder="Nº do brinco" />
                    <button type="submit" class="btn btn-secondary">Buscar</button>
                    <button v-if="brincoBusca || erroBusca" type="button" class="btn-link" @click="limparBusca">Limpar</button>
                </form>
            </div>
        </template>
        <section class="grid">
            <AnimalCard v-for="animal in animaisVisiveis" :key="animal.id" :animal="animal"
                :lote-compra="loteCompraPorId[animal.loteCompraId] || null"
                :lote-venda="loteVendaPorId[animal.loteVendaId] || null"
                @editar="animalEdicao = $event" @baixa="animalBaixa = $event" />
        </section>
    </PainelLista>

    <AnimalFormulario v-if="animalEdicao" :animal="animalEdicao" @salvo="aoEditar" @fechar="animalEdicao = null" />
    <DarBaixa v-if="animalBaixa" :animal="animalBaixa" @salvo="aoDarBaixa" @fechar="animalBaixa = null" />
</template>
