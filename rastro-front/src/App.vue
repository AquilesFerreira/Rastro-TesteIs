<script setup>
// App.vue: a "moldura" do sistema. Mostra o cabeçalho e troca a tela conforme o menu.
// Sem Vue Router: a tela atual é só uma variável (telaAtual).
import { ref } from 'vue'

import AppHeader from './components/AppHeader.vue'
import LoteCompraLista from './components/LoteCompraLista.vue'
import AnimalLista from './components/AnimalLista.vue'
import LoteVendaLista from './components/LoteVendaLista.vue'

const telaAtual = ref('lotes-compra')
</script>

<template>
    <AppHeader :tela-atual="telaAtual" @navegar="telaAtual = $event" />

    <main class="pagina">
        <LoteCompraLista v-if="telaAtual === 'lotes-compra'" />
        <AnimalLista v-else-if="telaAtual === 'animais'" />
        <LoteVendaLista v-else-if="telaAtual === 'lotes-venda'" />
    </main>
</template>
