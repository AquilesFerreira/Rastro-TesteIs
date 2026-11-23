<script setup>
// TELA de lotes de venda. Junta as peças:
//   service (fala com a API) + useListagem (estados da lista) + Card + Formulario + ConfirmarExclusao.
import { ref, computed, onMounted } from 'vue'
import { listarLotesVenda, excluirLoteVenda } from '../services/loteVendaService.js'
import { listarAnimais } from '../services/animalService.js'
import { useListagem } from '../composables/useListagem.js'
import { useFiltro } from '../composables/useFiltro.js'
import { extrairErroApi } from '../utils/erroApi.js'
import PainelLista from './PainelLista.vue'
import LoteVendaCard from './LoteVendaCard.vue'
import LoteVendaFormulario from './LoteVendaFormulario.vue'
import ConfirmarExclusao from './ConfirmarExclusao.vue'

const { itens: lotes, carregando, erro, vazio, total, carregar } = useListagem(listarLotesVenda)
const { filtros, limpar } = useFiltro({ busca: '' })

// A busca é feita no front, sobre a lista que já veio da API.
const lotesVisiveis = computed(() => {
    const termo = filtros.busca.trim().toLowerCase()
    if (!termo) return lotes.value
    return lotes.value.filter(l => l.comprador.toLowerCase().includes(termo))
})

// Brincos de cada venda: a API manda os ids (animaisIds); aqui viram números de brinco.
const brincoPorId = ref({})
async function carregarBrincos() {
    try {
        brincoPorId.value = Object.fromEntries((await listarAnimais()).map(a => [a.id, a.numeroBrinco]))
    } catch (e) { /* sem brincos, o card mostra só a quantidade */ }
}
function brincosDo(lote) {
    return (lote.animaisIds ?? []).map(id => brincoPorId.value[id]).filter(Boolean).sort((a, b) => a - b)
}
function atualizarTudo() { carregar(); carregarBrincos() }

// Formulário: null = fechado; { lote: null } = novo; { lote } = editando.
const formulario = ref(null)
function abrirNovo() { formulario.value = { lote: null } }
function abrirEdicao(lote) { formulario.value = { lote } }
function aoSalvar() { formulario.value = null; atualizarTudo() }

// Exclusão com confirmação.
const loteParaExcluir = ref(null)
const excluindo = ref(false)
const erroExclusao = ref('')
function pedirExclusao(lote) { loteParaExcluir.value = lote; erroExclusao.value = '' }
async function confirmarExclusao() {
    excluindo.value = true
    try {
        await excluirLoteVenda(loteParaExcluir.value.id)
        loteParaExcluir.value = null
        atualizarTudo()
    } catch (error) {
        erroExclusao.value = extrairErroApi(error, 'Não foi possível excluir o lote.').mensagem
    } finally {
        excluindo.value = false
    }
}

onMounted(() => { atualizarTudo() })
</script>

<template>
    <div class="pagina-cabecalho">
        <div>
            <h1>Lotes de venda</h1>
            <p>Os lotes que saíram da propriedade.</p>
        </div>
        <button type="button" class="btn btn-primary" @click="abrirNovo">Novo lote de venda</button>
    </div>

    <PainelLista titulo="Lotes" :total="total" :carregando="carregando" :erro="erro" :vazio="vazio"
        mensagem-vazio="Nenhum lote de venda cadastrado." @atualizar="atualizarTudo">
        <template #filtros>
            <div class="filtros">
                <input v-model="filtros.busca" type="search" placeholder="Buscar por comprador" />
                <button v-if="filtros.busca" type="button" class="btn-link" @click="limpar">Limpar</button>
            </div>
        </template>
        <section class="grid">
            <LoteVendaCard v-for="lote in lotesVisiveis" :key="lote.id" :lote="lote" :brincos="brincosDo(lote)"
                @editar="abrirEdicao" @excluir="pedirExclusao" />
        </section>
    </PainelLista>

    <LoteVendaFormulario v-if="formulario" :lote="formulario.lote"
        @salvo="aoSalvar" @fechar="formulario = null" />

    <ConfirmarExclusao v-if="loteParaExcluir"
        :mensagem="`Excluir a venda para ${loteParaExcluir.comprador} (#${loteParaExcluir.id})? Os ${loteParaExcluir.quantidade} animais voltam a ficar ativos.`"
        :excluindo="excluindo" :erro="erroExclusao"
        @confirmar="confirmarExclusao" @fechar="loteParaExcluir = null" />
</template>
