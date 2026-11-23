<script setup>
// Modal de baixa: o animal sai do rebanho ativo por morte, descarte ou outro motivo.
// Chama PATCH /animais/{id}/baixa.
import { ref } from 'vue'
import { darBaixaAnimal } from '../services/animalService.js'
import { extrairErroApi } from '../utils/erroApi.js'
import { formatarBrinco, MOTIVOS_BAIXA } from '../utils/formatadores.js'

const props = defineProps({ animal: { type: Object, required: true } })
const emit = defineEmits(['salvo', 'fechar'])

const motivo = ref('')
const data = ref(new Date().toISOString().slice(0, 10))
const salvando = ref(false)
const erro = ref('')

async function confirmar() {
    erro.value = ''
    if (!motivo.value) { erro.value = 'Escolha o motivo da baixa.'; return }
    salvando.value = true
    try {
        await darBaixaAnimal(props.animal.id, motivo.value, data.value)
        emit('salvo')
    } catch (e) {
        erro.value = extrairErroApi(e, 'Não foi possível dar baixa.').mensagem
    } finally {
        salvando.value = false
    }
}
</script>

<template>
    <div class="modal-overlay" @click.self="emit('fechar')">
        <div class="modal modal-pequeno">
            <div class="modal-header">
                <div>
                    <h2>Dar baixa</h2>
                    <p>{{ formatarBrinco(animal.numeroBrinco) }} · {{ animal.raca }}</p>
                </div>
                <button type="button" class="modal-fechar" @click="emit('fechar')">×</button>
            </div>
            <p v-if="erro" class="erro">{{ erro }}</p>
            <div class="campo">
                <span class="rotulo">Motivo</span>
                <div class="opcoes">
                    <button v-for="(rotulo, chave) in MOTIVOS_BAIXA" :key="chave" type="button" class="chip"
                        :class="{ ativo: motivo === chave }" @click="motivo = chave">{{ rotulo }}</button>
                </div>
            </div>
            <div class="campo">
                <label for="dataBaixa">Data</label>
                <input id="dataBaixa" v-model="data" type="date" />
            </div>
            <p class="ajuda">O animal deixa de aparecer como ativo e não pode mais ser vendido.</p>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="emit('fechar')">Cancelar</button>
                <button type="button" class="btn btn-danger" :disabled="salvando" @click="confirmar">
                    {{ salvando ? 'Salvando...' : 'Confirmar baixa' }}
                </button>
            </div>
        </div>
    </div>
</template>
