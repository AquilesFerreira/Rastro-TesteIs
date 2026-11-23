<script setup>
// Detalhe do lote de compra: os animais que entraram nele e como estão hoje.
// Aqui se corrige o lote: adicionar animal esquecido ou remover um cadastrado por engano.
import { ref, computed, onMounted } from 'vue'
import { listarAnimaisDoLote, adicionarAnimalAoLote, removerAnimalDoLote } from '../services/loteCompraService.js'
import { extrairErroApi } from '../utils/erroApi.js'
import { formatarBrinco, formatarData, STATUS_ANIMAL, MOTIVOS_BAIXA } from '../utils/formatadores.js'

const props = defineProps({ lote: { type: Object, required: true } })
const emit = defineEmits(['alterado', 'fechar'])

const animais = ref([])
const carregando = ref(true)
const erro = ref('')

async function carregar() {
    carregando.value = true
    try {
        animais.value = (await listarAnimaisDoLote(props.lote.id)).sort((a, b) => a.numeroBrinco - b.numeroBrinco)
    } catch (e) {
        erro.value = extrairErroApi(e, 'Não foi possível carregar os animais.').mensagem
    } finally {
        carregando.value = false
    }
}

const resumo = computed(() => {
    const r = { ATIVO: 0, VENDIDO: 0, BAIXA: 0 }
    animais.value.forEach(a => { r[a.status] = (r[a.status] || 0) + 1 })
    return r
})

// Adicionar um animal (repete os dados do último para agilizar)
const novo = ref({ numeroBrinco: '', sexo: 'M', castrado: 'N', raca: '' })
const salvando = ref(false)
async function adicionar() {
    erro.value = ''
    if (!novo.value.numeroBrinco || Number(novo.value.numeroBrinco) <= 0 || !novo.value.raca.trim()) {
        erro.value = 'Informe o brinco (maior que zero) e a raça.'
        return
    }
    salvando.value = true
    try {
        await adicionarAnimalAoLote(props.lote.id, {
            ...novo.value, numeroBrinco: Number(novo.value.numeroBrinco), raca: novo.value.raca.trim(),
        })
        novo.value.numeroBrinco = ''
        await carregar()
        emit('alterado')
    } catch (e) {
        erro.value = extrairErroApi(e, 'Não foi possível adicionar.').mensagem
    } finally {
        salvando.value = false
    }
}

async function remover(animal) {
    erro.value = ''
    try {
        await removerAnimalDoLote(props.lote.id, animal.id)
        await carregar()
        emit('alterado')
    } catch (e) {
        erro.value = extrairErroApi(e, 'Não foi possível remover.').mensagem
    }
}

onMounted(async () => {
    await carregar()
    const ultimo = animais.value[animais.value.length - 1]
    if (ultimo) novo.value = { numeroBrinco: '', sexo: ultimo.sexo, castrado: ultimo.castrado, raca: ultimo.raca }
})
</script>

<template>
    <div class="modal-overlay" @click.self="emit('fechar')">
        <div class="modal modal-largo">
            <div class="modal-header">
                <div>
                    <h2>Animais do lote #{{ lote.id }}</h2>
                    <p>{{ lote.fornecedor }} · {{ formatarData(lote.data) }}</p>
                </div>
                <button type="button" class="modal-fechar" @click="emit('fechar')">×</button>
            </div>

            <div class="opcoes">
                <span class="contador">{{ animais.length }} no lote</span>
                <span class="badge badge-ativo">{{ resumo.ATIVO }} ativos</span>
                <span class="badge badge-venda">{{ resumo.VENDIDO }} vendidos</span>
                <span class="badge badge-baixa">{{ resumo.BAIXA }} baixa</span>
            </div>

            <p v-if="erro" class="erro">{{ erro }}</p>
            <p v-if="carregando" class="ajuda">Carregando...</p>

            <div v-else class="tabela-wrap">
                <table class="tabela">
                    <thead>
                        <tr><th>Brinco</th><th>Sexo</th><th>Castrado</th><th>Raça</th><th>Situação</th><th class="col-acao"></th></tr>
                    </thead>
                    <tbody>
                        <tr v-for="a in animais" :key="a.id">
                            <td><strong>{{ formatarBrinco(a.numeroBrinco) }}</strong></td>
                            <td>{{ a.sexo === 'F' ? 'Fêmea' : 'Macho' }}</td>
                            <td>{{ a.castrado === 'S' ? 'Sim' : 'Não' }}</td>
                            <td>{{ a.raca }}</td>
                            <td>
                                <span class="badge" :class="{ 'badge-ativo': a.status === 'ATIVO', 'badge-venda': a.status === 'VENDIDO', 'badge-baixa': a.status === 'BAIXA' }">
                                    {{ STATUS_ANIMAL[a.status] }}<template v-if="a.status === 'BAIXA'"> · {{ MOTIVOS_BAIXA[a.motivoBaixa] }}</template>
                                </span>
                            </td>
                            <td class="col-acao">
                                <button v-if="a.status === 'ATIVO'" type="button" class="btn-link-danger"
                                    title="Cadastrado por engano" @click="remover(a)">Remover</button>
                            </td>
                        </tr>
                        <!-- Linha para adicionar um animal esquecido -->
                        <tr>
                            <td><input v-model="novo.numeroBrinco" type="number" min="1" placeholder="Nº brinco" /></td>
                            <td><select v-model="novo.sexo"><option value="M">Macho</option><option value="F">Fêmea</option></select></td>
                            <td><select v-model="novo.castrado"><option value="N">Não</option><option value="S">Sim</option></select></td>
                            <td><input v-model="novo.raca" type="text" placeholder="Raça" /></td>
                            <td></td>
                            <td class="col-acao">
                                <button type="button" class="btn btn-primary" :disabled="salvando" @click="adicionar">Adicionar</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <p class="ajuda">"Remover" é para animal lançado por engano (brinco errado). Morte ou descarte se registra como <strong>baixa</strong> na tela de Animais.</p>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="emit('fechar')">Fechar</button>
            </div>
        </div>
    </div>
</template>
