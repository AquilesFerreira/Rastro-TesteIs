<script setup>
// Confirmação que sobe por cima da tela antes de excluir (a mesma ideia do protótipo).
defineProps({
    titulo: { type: String, default: 'Confirmar exclusão' },
    mensagem: { type: String, required: true },
    excluindo: Boolean,
    erro: String,
})
defineEmits(['confirmar', 'fechar'])
</script>

<template>
    <div class="modal-overlay" @click.self="$emit('fechar')">
        <div class="modal modal-pequeno">
            <div class="modal-header">
                <h2>{{ titulo }}</h2>
            </div>
            <p>{{ mensagem }}</p>
            <p v-if="erro" class="erro">{{ erro }}</p>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="$emit('fechar')">Cancelar</button>
                <button type="button" class="btn btn-danger" :disabled="excluindo" @click="$emit('confirmar')">
                    {{ excluindo ? 'Excluindo...' : 'Excluir' }}
                </button>
            </div>
        </div>
    </div>
</template>
