<script setup>
// Cabeçalho com o menu das 3 telas. Avisa o App.vue qual tela abrir (evento "navegar").
defineProps({ telaAtual: { type: String, required: true } })
defineEmits(['navegar'])

const itens = [
    { id: 'lotes-compra', rotulo: 'Lotes de compra' },
    { id: 'animais', rotulo: 'Animais' },
    { id: 'lotes-venda', rotulo: 'Lotes de venda' },
]
</script>

<template>
    <header class="app-header">
        <div class="container header-content">
            <div class="marca">
                <strong>Rastro</strong>
                <span>Do brinco à venda</span>
            </div>
            <nav class="menu">
                <button
                    v-for="item in itens"
                    :key="item.id"
                    type="button"
                    class="menu-item"
                    :class="{ ativo: telaAtual === item.id }"
                    @click="$emit('navegar', item.id)"
                >
                    {{ item.rotulo }}
                </button>
            </nav>
        </div>
    </header>
</template>
