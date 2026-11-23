<script setup>
// Formulário de lote de compra.
//  - NOVO (POST): cabeçalho + a lista de animais. A quantidade é contada, não digitada.
//  - EDITAR (PUT): só o cabeçalho. Os animais se corrigem no botão "Animais" do card.
import { ref, computed } from 'vue'
import { cadastrarLoteCompra, atualizarLoteCompra } from '../services/loteCompraService.js'
import { extrairErroApi } from '../utils/erroApi.js'

const props = defineProps({ lote: { type: Object, default: null } })
const emit = defineEmits(['salvo', 'fechar'])

const editando = computed(() => props.lote !== null)

// Os nomes dos campos são os mesmos do LoteCompraRequestDTO da API.
const formulario = ref({
    data: props.lote?.data ?? '',
    fornecedor: props.lote?.fornecedor ?? '',
    pesoTotal: props.lote?.pesoTotal ?? '',
    valorTotal: props.lote?.valorTotal ?? '',
})

// Linhas de animais (só no cadastro). Cada linha nova copia sexo/castrado/raça
// da anterior: num lote é comum vir tudo igual, então só se digita o brinco.
const animais = ref([{ numeroBrinco: '', sexo: 'M', castrado: 'N', raca: '' }])
function adicionarLinha() {
    const ultima = animais.value[animais.value.length - 1]
    animais.value.push({ numeroBrinco: '', sexo: ultima?.sexo ?? 'M', castrado: ultima?.castrado ?? 'N', raca: ultima?.raca ?? '' })
}
function removerLinha(i) {
    if (animais.value.length > 1) animais.value.splice(i, 1)
}
const quantidade = computed(() => animais.value.length)

const tentouEnviar = ref(false)
const salvando = ref(false)
const erroFormulario = ref('')
const errosApi = ref({})

// Validação no front (espelha as anotações do DTO e as regras do service).
const erros = computed(() => {
    const e = {}
    const f = formulario.value
    if (!f.data) e.data = 'Informe a data da compra.'
    if (!f.fornecedor.trim()) e.fornecedor = 'Informe o fornecedor.'
    if (f.pesoTotal === '' || Number(f.pesoTotal) <= 0) e.pesoTotal = 'O peso total deve ser maior que zero.'
    if (f.valorTotal === '' || Number(f.valorTotal) <= 0) e.valorTotal = 'O valor total deve ser maior que zero.'
    if (!editando.value) {
        const vistos = new Set()
        animais.value.forEach((a, i) => {
            const n = Number(a.numeroBrinco)
            if (a.numeroBrinco === '' || n <= 0) e[`brinco${i}`] = 'Brinco > 0'
            else if (vistos.has(n)) e[`brinco${i}`] = 'Repetido'
            vistos.add(n)
            if (!a.raca.trim()) e[`raca${i}`] = 'Informe a raça'
        })
    }
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
        fornecedor: f.fornecedor.trim(),
        pesoTotal: Number(f.pesoTotal),
        valorTotal: Number(f.valorTotal),
    }

    try {
        if (editando.value) {
            await atualizarLoteCompra(props.lote.id, dados)
        } else {
            dados.animais = animais.value.map(a => ({
                numeroBrinco: Number(a.numeroBrinco),
                sexo: a.sexo,
                castrado: a.castrado,
                raca: a.raca.trim(),
            }))
            await cadastrarLoteCompra(dados)
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
        <div class="modal" :class="{ 'modal-largo': !editando }">
            <div class="modal-header">
                <div>
                    <h2>{{ editando ? 'Editar lote de compra' : 'Novo lote de compra' }}</h2>
                    <p>{{ editando ? `Lote #${lote.id} · ${lote.quantidade} animais` : 'Dados da compra e os animais que vieram nela.' }}</p>
                </div>
                <button type="button" class="modal-fechar" @click="emit('fechar')">×</button>
            </div>

            <form @submit.prevent="salvar">
                <p v-if="erroFormulario" class="erro">{{ erroFormulario }}</p>

                <div class="form-row">
                    <div class="campo">
                        <label for="fornecedor">Fornecedor</label>
                        <input id="fornecedor" v-model="formulario.fornecedor" type="text"
                            placeholder="Nome do fornecedor" :class="{ 'input-error': erroDoCampo('fornecedor') }" />
                        <span v-if="erroDoCampo('fornecedor')" class="field-error">{{ erroDoCampo('fornecedor') }}</span>
                    </div>
                    <div class="campo">
                        <label for="data">Data da compra</label>
                        <input id="data" v-model="formulario.data" type="date"
                            :class="{ 'input-error': erroDoCampo('data') }" />
                        <span v-if="erroDoCampo('data')" class="field-error">{{ erroDoCampo('data') }}</span>
                    </div>
                </div>

                <div class="form-row">
                    <div class="campo">
                        <label for="pesoTotal">Peso total (kg)</label>
                        <input id="pesoTotal" v-model="formulario.pesoTotal" type="number" min="0" step="0.01"
                            placeholder="Ex.: 3840" :class="{ 'input-error': erroDoCampo('pesoTotal') }" />
                        <span v-if="erroDoCampo('pesoTotal')" class="field-error">{{ erroDoCampo('pesoTotal') }}</span>
                    </div>
                    <div class="campo">
                        <label for="valorTotal">Valor total (R$)</label>
                        <input id="valorTotal" v-model="formulario.valorTotal" type="number" min="0" step="0.01"
                            placeholder="Ex.: 45600" :class="{ 'input-error': erroDoCampo('valorTotal') }" />
                        <span v-if="erroDoCampo('valorTotal')" class="field-error">{{ erroDoCampo('valorTotal') }}</span>
                    </div>
                </div>

                <!-- Animais do lote (só no cadastro) -->
                <template v-if="!editando">
                    <div class="secao-titulo">
                        <h3>Animais do lote</h3>
                        <span class="contador">{{ quantidade }} {{ quantidade === 1 ? 'animal' : 'animais' }}</span>
                    </div>
                    <p class="ajuda">A quantidade do lote é o número de linhas. Cada linha nova repete sexo, castrado e raça da anterior.</p>
                    <div class="tabela-wrap">
                        <table class="tabela">
                            <thead>
                                <tr><th class="num">#</th><th>Brinco</th><th>Sexo</th><th>Castrado</th><th>Raça</th><th class="col-acao"></th></tr>
                            </thead>
                            <tbody>
                                <tr v-for="(a, i) in animais" :key="i">
                                    <td class="num">{{ i + 1 }}</td>
                                    <td>
                                        <input v-model="a.numeroBrinco" type="number" min="1" placeholder="Nº"
                                            :class="{ 'input-error': erroDoCampo(`brinco${i}`) }" :title="erroDoCampo(`brinco${i}`)" />
                                    </td>
                                    <td>
                                        <select v-model="a.sexo"><option value="M">Macho</option><option value="F">Fêmea</option></select>
                                    </td>
                                    <td>
                                        <select v-model="a.castrado"><option value="N">Não</option><option value="S">Sim</option></select>
                                    </td>
                                    <td>
                                        <input v-model="a.raca" type="text" placeholder="Ex.: Angus"
                                            :class="{ 'input-error': erroDoCampo(`raca${i}`) }" :title="erroDoCampo(`raca${i}`)" />
                                    </td>
                                    <td class="col-acao">
                                        <button type="button" class="btn-link-danger" :disabled="animais.length === 1"
                                            title="Remover linha" @click="removerLinha(i)">×</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <span v-if="tentouEnviar && Object.keys(erros).some(k => k.startsWith('brinco') || k.startsWith('raca'))" class="field-error">
                        Confira os brincos (maior que zero, sem repetir) e a raça de cada animal.
                    </span>
                    <div>
                        <button type="button" class="btn btn-secondary" @click="adicionarLinha">+ Adicionar animal</button>
                    </div>
                </template>
                <p v-else class="ajuda">Para corrigir os animais deste lote, use o botão <strong>Animais</strong> no card.</p>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" @click="emit('fechar')">Cancelar</button>
                    <button type="submit" class="btn btn-primary" :disabled="salvando">
                        {{ salvando ? 'Salvando...' : (editando ? 'Salvar alterações' : `Registrar lote com ${quantidade} ${quantidade === 1 ? 'animal' : 'animais'}`) }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
