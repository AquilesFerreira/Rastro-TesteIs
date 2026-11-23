<script setup>
// TELA de lotes de compra. Junta as peças:
//   service (fala com a API) + useListagem (estados da lista) + Card + Formulario + ConfirmarExclusao.
import { ref, computed, onMounted } from 'vue'
import { listarLotesCompra, excluirLoteCompra } from '../services/loteCompraService.js'
import { useListagem } from '../composables/useListagem.js'
import { useFiltro } from '../composables/useFiltro.js'
import { extrairErroApi } from '../utils/erroApi.js'
import PainelLista from './PainelLista.vue'
import LoteCompraCard from './LoteCompraCard.vue'
import LoteCompraFormulario from './LoteCompraFormulario.vue'
import ConfirmarExclusao from './ConfirmarExclusao.vue'
import LoteCompraAnimais from './LoteCompraAnimais.vue'

const { itens: lotes, carregando, erro, vazio, total, carregar } = useListagem(listarLotesCompra)
const { filtros, limpar } = useFiltro({ busca: '' })

// A busca é feita no front, sobre a lista que já veio da API.
const lotesVisiveis = computed(() => {
    const termo = filtros.busca.trim().toLowerCase()
    if (!termo) return lotes.value
    return lotes.value.filter(l => l.fornecedor.toLowerCase().includes(termo))
})

// Formulário: null = fechado; { lote: null } = novo; { lote } = editando.
const formulario = ref(null)
function abrirNovo() { formulario.value = { lote: null } }
function abrirEdicao(lote) { formulario.value = { lote } }
function aoSalvar() { formulario.value = null; carregar() }

// Detalhe com os animais do lote (null = fechado)
const loteDetalhe = ref(null)

// Exclusão com confirmação.
const loteParaExcluir = ref(null)
const excluindo = ref(false)
const erroExclusao = ref('')
function pedirExclusao(lote) { loteParaExcluir.value = lote; erroExclusao.value = '' }
async function confirmarExclusao() {
    excluindo.value = true
    try {
        await excluirLoteCompra(loteParaExcluir.value.id)
        loteParaExcluir.value = null
        carregar()
    } catch (error) {
        // Ex.: a API recusa excluir um lote que tem animais vendidos ou com baixa.
        erroExclusao.value = extrairErroApi(error, 'Não foi possível excluir o lote.').mensagem
    } finally {
        excluindo.value = false
    }
}

onMounted(() => { carregar() })
</script>

<template>
    <div class="pagina-cabecalho">
        <div>
            <h1>Lotes de compra</h1>
            <p>Os lotes que entraram na propriedade.</p>
        </div>
        <button type="button" class="btn btn-primary" @click="abrirNovo">Novo lote de compra</button>
    </div>

    <PainelLista titulo="Lotes" :total="total" :carregando="carregando" :erro="erro" :vazio="vazio"
        mensagem-vazio="Nenhum lote de compra cadastrado." @atualizar="carregar">
        <template #filtros>
            <div class="filtros">
                <input v-model="filtros.busca" type="search" placeholder="Buscar por fornecedor" />
                <button v-if="filtros.busca" type="button" class="btn-link" @click="limpar">Limpar</button>
            </div>
        </template>
        <section class="grid">
            <LoteCompraCard v-for="lote in lotesVisiveis" :key="lote.id" :lote="lote"
                @animais="loteDetalhe = $event" @editar="abrirEdicao" @excluir="pedirExclusao" />
        </section>
    </PainelLista>

    <LoteCompraFormulario v-if="formulario" :lote="formulario.lote"
        @salvo="aoSalvar" @fechar="formulario = null" />

    <LoteCompraAnimais v-if="loteDetalhe" :lote="loteDetalhe"
        @alterado="carregar" @fechar="loteDetalhe = null" />

    <ConfirmarExclusao v-if="loteParaExcluir"
        :mensagem="`Excluir a compra de ${loteParaExcluir.fornecedor} (#${loteParaExcluir.id}) e os ${loteParaExcluir.quantidade} animais dela? Use só para lançamento errado. Essa ação não pode ser desfeita.`"
        :excluindo="excluindo" :erro="erroExclusao"
        @confirmar="confirmarExclusao" @fechar="loteParaExcluir = null" />
</template>
