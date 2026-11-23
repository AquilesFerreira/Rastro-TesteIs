<script setup>
// Edição básica do animal (PUT /animais/{id}): corrige brinco, sexo, castrado e raça.
// Não troca lote nem status: o animal entra pelo lote de compra e sai pela venda ou baixa.
import { ref, computed } from 'vue'
import { atualizarAnimal } from '../services/animalService.js'
import { extrairErroApi } from '../utils/erroApi.js'
import { formatarBrinco } from '../utils/formatadores.js'

const props = defineProps({ animal: { type: Object, required: true } })
const emit = defineEmits(['salvo', 'fechar'])

const formulario = ref({
    numeroBrinco: props.animal.numeroBrinco,
    sexo: props.animal.sexo,
    castrado: props.animal.castrado,
    raca: props.animal.raca,
})

const tentouEnviar = ref(false)
const salvando = ref(false)
const erroFormulario = ref('')
const errosApi = ref({})

const erros = computed(() => {
    const e = {}
    const f = formulario.value
    if (f.numeroBrinco === '' || Number(f.numeroBrinco) <= 0) e.numeroBrinco = 'O brinco deve ser maior que zero.'
    if (!String(f.raca).trim()) e.raca = 'Informe a raça.'
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
    try {
        await atualizarAnimal(props.animal.id, {
            numeroBrinco: Number(f.numeroBrinco), sexo: f.sexo, castrado: f.castrado, raca: String(f.raca).trim(),
        })
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
        <div class="modal">
            <div class="modal-header">
                <div>
                    <h2>Editar animal</h2>
                    <p>{{ formatarBrinco(animal.numeroBrinco) }} · lote de compra #{{ animal.loteCompraId }}</p>
                </div>
                <button type="button" class="modal-fechar" @click="emit('fechar')">×</button>
            </div>

            <form @submit.prevent="salvar">
                <p v-if="erroFormulario" class="erro">{{ erroFormulario }}</p>

                <div class="form-row">
                    <div class="campo">
                        <label for="numeroBrinco">Número do brinco</label>
                        <input id="numeroBrinco" v-model="formulario.numeroBrinco" type="number" min="1"
                            :class="{ 'input-error': erroDoCampo('numeroBrinco') }" />
                        <span v-if="erroDoCampo('numeroBrinco')" class="field-error">{{ erroDoCampo('numeroBrinco') }}</span>
                    </div>
                    <div class="campo">
                        <label for="raca">Raça</label>
                        <input id="raca" v-model="formulario.raca" type="text"
                            :class="{ 'input-error': erroDoCampo('raca') }" />
                        <span v-if="erroDoCampo('raca')" class="field-error">{{ erroDoCampo('raca') }}</span>
                    </div>
                </div>

                <div class="form-row">
                    <div class="campo">
                        <span class="rotulo">Sexo</span>
                        <div class="opcoes">
                            <button type="button" class="chip" :class="{ ativo: formulario.sexo === 'M' }" @click="formulario.sexo = 'M'">Macho</button>
                            <button type="button" class="chip" :class="{ ativo: formulario.sexo === 'F' }" @click="formulario.sexo = 'F'">Fêmea</button>
                        </div>
                    </div>
                    <div class="campo">
                        <span class="rotulo">Castrado</span>
                        <div class="opcoes">
                            <button type="button" class="chip" :class="{ ativo: formulario.castrado === 'S' }" @click="formulario.castrado = 'S'">Sim</button>
                            <button type="button" class="chip" :class="{ ativo: formulario.castrado === 'N' }" @click="formulario.castrado = 'N'">Não</button>
                        </div>
                    </div>
                </div>

                <p class="ajuda">Lote e situação não mudam aqui: o animal entra pelo lote de compra e sai pela venda ou pela baixa.</p>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" @click="emit('fechar')">Cancelar</button>
                    <button type="submit" class="btn btn-primary" :disabled="salvando">
                        {{ salvando ? 'Salvando...' : 'Salvar alterações' }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
