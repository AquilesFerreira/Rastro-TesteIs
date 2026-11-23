<script setup>
// Um card = um animal. Recebe o animal e os lotes já resolvidos pela lista.
// Ações: editar (dados básicos) e dar baixa (só se ainda estiver ativo).
import { computed } from 'vue'
import { formatarBrinco, formatarData, STATUS_ANIMAL, MOTIVOS_BAIXA } from '../utils/formatadores.js'

const props = defineProps({
    animal: { type: Object, required: true },
    loteCompra: { type: Object, default: null },
    loteVenda: { type: Object, default: null },
})
defineEmits(['editar', 'baixa'])

const classeStatus = computed(() => ({
    ATIVO: 'badge-ativo', VENDIDO: 'badge-venda', BAIXA: 'badge-baixa',
}[props.animal.status]))
const sexo = computed(() => (props.animal.sexo === 'F' ? 'Fêmea' : 'Macho'))
const castrado = computed(() => (props.animal.castrado === 'S' ? 'castrado' : 'inteiro'))
</script>

<template>
    <article class="card">
        <div class="card-topo">
            <h3>{{ formatarBrinco(animal.numeroBrinco) }}</h3>
            <span class="badge" :class="classeStatus">{{ STATUS_ANIMAL[animal.status] }}</span>
        </div>
        <p class="card-sub">{{ animal.raca }} · {{ sexo }} {{ castrado }}</p>
        <dl class="card-dados">
            <div>
                <dt>Entrou (compra)</dt>
                <dd>{{ loteCompra ? `#${loteCompra.id} · ${loteCompra.fornecedor}` : `#${animal.loteCompraId}` }}</dd>
            </div>
            <div v-if="animal.status === 'VENDIDO'">
                <dt>Saiu (venda)</dt>
                <dd>{{ loteVenda ? `#${loteVenda.id} · ${loteVenda.comprador}` : `#${animal.loteVendaId}` }}</dd>
            </div>
            <div v-if="animal.status === 'BAIXA'">
                <dt>Baixa</dt>
                <dd>{{ MOTIVOS_BAIXA[animal.motivoBaixa] }} · {{ formatarData(animal.dataBaixa) }}</dd>
            </div>
        </dl>
        <footer class="card-acoes">
            <button type="button" class="btn btn-secondary" @click="$emit('editar', animal)">Editar</button>
            <button v-if="animal.status === 'ATIVO'" type="button" class="btn btn-link-danger" @click="$emit('baixa', animal)">Dar baixa</button>
        </footer>
    </article>
</template>
