<script setup>
// Um card = um lote de compra. Mostra os dados e como estão os animais do lote hoje.
import { computed } from 'vue'
import { formatarData, formatarMoeda, formatarPeso, pesoMedio } from '../utils/formatadores.js'

const props = defineProps({ lote: { type: Object, required: true } })
defineEmits(['animais', 'editar', 'excluir'])

const media = computed(() => formatarPeso(pesoMedio(props.lote.pesoTotal, props.lote.quantidade)))
</script>

<template>
    <article class="card">
        <div class="card-topo">
            <h3>{{ lote.fornecedor }}</h3>
            <span class="badge badge-compra">Compra</span>
        </div>
        <p class="card-sub">{{ formatarData(lote.data) }} · {{ lote.quantidade }} {{ lote.quantidade === 1 ? 'animal' : 'animais' }}</p>
        <div class="opcoes">
            <span class="badge badge-ativo">{{ lote.ativos }} ativos</span>
            <span v-if="lote.vendidos" class="badge badge-venda">{{ lote.vendidos }} vendidos</span>
            <span v-if="lote.baixas" class="badge badge-baixa">{{ lote.baixas }} baixa</span>
        </div>
        <dl class="card-dados">
            <div><dt>Peso total</dt><dd>{{ formatarPeso(lote.pesoTotal) }}</dd></div>
            <div><dt>Peso médio</dt><dd>{{ media }}</dd></div>
            <div><dt>Valor total</dt><dd class="valor">{{ formatarMoeda(lote.valorTotal) }}</dd></div>
        </dl>
        <footer class="card-acoes">
            <button type="button" class="btn btn-primary" @click="$emit('animais', lote)">Animais</button>
            <button type="button" class="btn btn-secondary" @click="$emit('editar', lote)">Editar</button>
            <button type="button" class="btn btn-link-danger" @click="$emit('excluir', lote)">Excluir</button>
        </footer>
    </article>
</template>
