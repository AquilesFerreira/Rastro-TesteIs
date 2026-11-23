<script setup>
// Formulário de lote de venda. CADASTRAR (POST) ou EDITAR (PUT).
// A venda é montada SELECIONANDO os animais ativos, agrupados pelo lote de compra.
// A quantidade é contada pelos animais marcados.
import { ref, computed, onMounted } from 'vue'
import { cadastrarLoteVenda, atualizarLoteVenda } from '../services/loteVendaService.js'
import { listarAnimais } from '../services/animalService.js'
import { listarLotesCompra } from '../services/loteCompraService.js'
import { extrairErroApi } from '../utils/erroApi.js'
import { formatarBrinco } from '../utils/formatadores.js'

const props = defineProps({ lote: { type: Object, default: null } })
const emit = defineEmits(['salvo', 'fechar'])

const editando = computed(() => props.lote !== null)

// Os nomes dos campos são os mesmos do LoteVendaRequestDTO da API.
const formulario = ref({
    data: props.lote?.data ?? '',
    comprador: props.lote?.comprador ?? '',
    pesoTotal: props.lote?.pesoTotal ?? '',
    valorTotal: props.lote?.valorTotal ?? '',
})

// Animais que podem entrar na venda: os ATIVOS + (na edição) os que já estão nesta venda.
const animais = ref([])
const lotesCompra = ref([])
const carregandoAnimais = ref(true)
const selecionados = ref(new Set(props.lote?.animaisIds ?? []))

onMounted(async () => {
    try {
        const [todos, compras] = await Promise.all([listarAnimais(), listarLotesCompra()])
        animais.value = todos.filter(a => a.status === 'ATIVO' || (editando.value && a.loteVendaId === props.lote.id))
        lotesCompra.value = compras
    } catch (e) {
        erroFormulario.value = extrairErroApi(e, 'Não foi possível carregar os animais.').mensagem
    } finally {
        carregandoAnimais.value = false
    }
})

// Busca rápida por brinco dentro da lista
const filtroBrinco = ref('')

// Agrupa por lote de compra: [{ lote, animais: [...] }]
const grupos = computed(() => {
    const porLote = {}
    animais.value
        .filter(a => !filtroBrinco.value || String(a.numeroBrinco).includes(filtroBrinco.value))
        .sort((a, b) => a.numeroBrinco - b.numeroBrinco)
        .forEach(a => { (porLote[a.loteCompraId] ??= []).push(a) })
    return Object.entries(porLote).map(([id, lista]) => ({
        lote: lotesCompra.value.find(l => l.id === Number(id)) ?? { id: Number(id), fornecedor: '' },
        animais: lista,
    }))
})

function alternar(id) {
    const s = new Set(selecionados.value)
    s.has(id) ? s.delete(id) : s.add(id)
    selecionados.value = s
}
function todosMarcados(grupo) { return grupo.animais.every(a => selecionados.value.has(a.id)) }
function alternarGrupo(grupo) {
    const s = new Set(selecionados.value)
    const marcar = !todosMarcados(grupo)
    grupo.animais.forEach(a => (marcar ? s.add(a.id) : s.delete(a.id)))
    selecionados.value = s
}
const quantidade = computed(() => selecionados.value.size)

const tentouEnviar = ref(false)
const salvando = ref(false)
const erroFormulario = ref('')
const errosApi = ref({})

const erros = computed(() => {
    const e = {}
    const f = formulario.value
    if (!f.data) e.data = 'Informe a data da venda.'
    if (!f.comprador.trim()) e.comprador = 'Informe o comprador.'
    if (f.pesoTotal === '' || Number(f.pesoTotal) <= 0) e.pesoTotal = 'O peso total deve ser maior que zero.'
    if (f.valorTotal === '' || Number(f.valorTotal) <= 0) e.valorTotal = 'O valor total deve ser maior que zero.'
    if (quantidade.value === 0) e.animaisIds = 'Selecione pelo menos um animal.'
    return e
})

function erroDoCampo(campo) {
    return errosApi.value[campo] || (tentouEnviar.value ? erros.value[campo] : '')
}

async function salvar() {
    tentouEnviar.value = true
    errosApi.value = {}
    if (Object.keys(erros.value).length) return

    salvando.value = true
    erroFormulario.value = ''
    const f = formulario.value
    const dados = {
        data: f.data,
        comprador: f.comprador.trim(),
        pesoTotal: Number(f.pesoTotal),
        valorTotal: Number(f.valorTotal),
        animaisIds: [...selecionados.value],
    }

    try {
        if (editando.value) {
            await atualizarLoteVenda(props.lote.id, dados)
        } else {
            await cadastrarLoteVenda(dados)
        }
        emit('salvo')
    } catch (error) {
        const { mensagem, campos } = extrairErroApi(error)
        erroFormulario.value = mensagem
        errosApi.value = campos
    } finally {
        salvando.value = false
    }
}
</script>

<template>
    <div class="modal-overlay" @click.self="emit('fechar')">
        <div class="modal modal-largo">
            <div class="modal-header">
                <div>
                    <h2>{{ editando ? 'Editar lote de venda' : 'Novo lote de venda' }}</h2>
                    <p>{{ editando ? `Lote #${lote.id}` : 'Dados da venda e os animais que saem nela.' }}</p>
                </div>
                <button type="button" class="modal-fechar" @click="emit('fechar')">×</button>
            </div>

            <form @submit.prevent="salvar">
                <p v-if="erroFormulario" class="erro">{{ erroFormulario }}</p>

                <div class="form-row">
                    <div class="campo">
                        <label for="comprador">Comprador</label>
                        <input id="comprador" v-model="formulario.comprador" type="text"
                            placeholder="Nome do comprador" :class="{ 'input-error': erroDoCampo('comprador') }" />
                        <span v-if="erroDoCampo('comprador')" class="field-error">{{ erroDoCampo('comprador') }}</span>
                    </div>
                    <div class="campo">
                        <label for="data">Data da venda</label>
                        <input id="data" v-model="formulario.data" type="date"
                            :class="{ 'input-error': erroDoCampo('data') }" />
                        <span v-if="erroDoCampo('data')" class="field-error">{{ erroDoCampo('data') }}</span>
                    </div>
                </div>

                <div class="form-row">
                    <div class="campo">
                        <label for="pesoTotal">Peso total (kg)</label>
                        <input id="pesoTotal" v-model="formulario.pesoTotal" type="number" min="0" step="0.01"
                            placeholder="Ex.: 1561" :class="{ 'input-error': erroDoCampo('pesoTotal') }" />
                        <span v-if="erroDoCampo('pesoTotal')" class="field-error">{{ erroDoCampo('pesoTotal') }}</span>
                    </div>
                    <div class="campo">
                        <label for="valorTotal">Valor total (R$)</label>
                        <input id="valorTotal" v-model="formulario.valorTotal" type="number" min="0" step="0.01"
                            placeholder="Ex.: 32000" :class="{ 'input-error': erroDoCampo('valorTotal') }" />
                        <span v-if="erroDoCampo('valorTotal')" class="field-error">{{ erroDoCampo('valorTotal') }}</span>
                    </div>
                </div>

                <div class="secao-titulo">
                    <h3>Animais vendidos</h3>
                    <span class="contador">{{ quantidade }} {{ quantidade === 1 ? 'selecionado' : 'selecionados' }}</span>
                </div>
                <input v-model="filtroBrinco" type="search" class="campo-busca" placeholder="Filtrar por nº do brinco"
                    style="padding:8px 12px;border:1px solid var(--borda);border-radius:8px;font:inherit" />

                <p v-if="carregandoAnimais" class="ajuda">Carregando animais...</p>
                <p v-else-if="animais.length === 0" class="ajuda">Nenhum animal ativo para vender. Cadastre um lote de compra primeiro.</p>
                <div v-for="g in grupos" :key="g.lote.id" class="selecao-grupo">
                    <header>
                        <strong>Lote #{{ g.lote.id }} · {{ g.lote.fornecedor }}</strong>
                        <button type="button" class="btn-link" @click="alternarGrupo(g)">
                            {{ todosMarcados(g) ? 'Desmarcar todos' : `Marcar todos (${g.animais.length})` }}
                        </button>
                    </header>
                    <div class="selecao-itens">
                        <label v-for="a in g.animais" :key="a.id" class="selecao-item" :class="{ marcado: selecionados.has(a.id) }">
                            <input type="checkbox" :checked="selecionados.has(a.id)" @change="alternar(a.id)" />
                            {{ formatarBrinco(a.numeroBrinco) }} · {{ a.sexo }}
                        </label>
                    </div>
                </div>
                <span v-if="erroDoCampo('animaisIds')" class="field-error">{{ erroDoCampo('animaisIds') }}</span>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" @click="emit('fechar')">Cancelar</button>
                    <button type="submit" class="btn btn-primary" :disabled="salvando">
                        {{ salvando ? 'Salvando...' : (editando ? 'Salvar alterações' : `Registrar venda de ${quantidade} ${quantidade === 1 ? 'animal' : 'animais'}`) }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
