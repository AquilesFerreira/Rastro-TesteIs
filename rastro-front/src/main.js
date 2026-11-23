// Ponto de entrada: cria o app Vue, carrega o CSS global e monta tudo na <div id="app"> do index.html.
import { createApp } from 'vue'
import './assets/base.css'
import App from './App.vue'

createApp(App).mount('#app')
