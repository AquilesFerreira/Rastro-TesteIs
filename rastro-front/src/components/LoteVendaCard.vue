<script setup>
// Um card = um lote de venda. Só mostra os dados e avisa a lista quando clicam em Editar ou Excluir.
import { computed } from 'vue'
import { formatarData, formatarMoeda, formatarPeso, pesoMedio, formatarBrinco } from '../utils/formatadores.js'

const props = defineProps({
    lote: { type: Object, required: true },
    brincos: { type: Array, default: () => [] },   // números dos brincos vendidos, resolvidos pela lista
})
defineEmits(['editar', 'excluir'])

const media = computed(() => formatarPeso(pesoMedio(props.lote.pesoTotal, props.lote.quantidade)))
</script>

<template>
    <article class="card">
        <div class="card-topo">
            <h3>{{ lote.comprador }}</h3>
            <span class="badge badge-venda">Venda</span>
        </div>
        <p class="card-sub">{{ formatarData(lote.data) }} · {{ lote.quantidade }} {{ lote.quantidade === 1 ? 'animal' : 'animais' }}</p>
        <dl class="card-dados">
            <div><dt>Peso total</dt><dd>{{ formatarPeso(lote.pesoTotal) }}</dd></div>
            <div><dt>Peso médio</dt><dd>{{ media }}</dd></div>
            <div><dt>Valor total</dt><dd class="valor">{{ formatarMoeda(lote.valorTotal) }}</dd></div>
        </dl>
        <div v-if="brincos.length" class="lista-brincos">
            <span v-for="b in brincos" :key="b">{{ formatarBrinco(b) }}</span>
        </div>
        <footer class="card-acoes">
            <button type="button" class="btn btn-secondary" @click="$emit('editar', lote)">Editar</button>
            <button type="button" class="btn btn-link-danger" @click="$emit('excluir', lote)">Excluir</button>
        </footer>
    </article>
</template>
