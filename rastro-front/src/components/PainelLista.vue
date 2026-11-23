<script setup>
// Painel genérico de lista.
// mostra o esqueleto enquanto carrega, o erro, a mensagem de vazio ou a lista.
defineProps({
    titulo: String,
    total: { type: Number, default: 0 },
    carregando: Boolean,
    erro: String,
    vazio: Boolean,
    mensagemVazio: { type: String, default: 'Nenhum item encontrado.' },
})
defineEmits(['atualizar'])
</script>

<template>
    <section>
        <div class="painel-header">
            <h2>{{ titulo }} <span v-if="total">({{ total }})</span></h2>
            <div class="painel-acoes">
                <slot name="acoes" />
                <button type="button" class="btn btn-secondary" @click="$emit('atualizar')">Atualizar</button>
            </div>
        </div>
        <slot name="filtros" />
        <div v-if="carregando" class="skeleton-grid">
            <div v-for="n in 6" :key="n" class="skeleton-card">
                <div class="skeleton-linha skeleton-titulo"></div>
                <div class="skeleton-linha"></div>
                <div class="skeleton-linha skeleton-curta"></div>
            </div>
        </div>
        <p v-else-if="erro" class="erro">{{ erro }}</p>
        <p v-else-if="vazio" class="vazio">{{ mensagemVazio }}</p>
        <slot v-else />
    </section>
</template>
