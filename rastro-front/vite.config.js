import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// O Vite sobe o front em http://localhost:5173 (a mesma origem liberada no CorsConfig da API).
export default defineConfig({
  plugins: [vue()],
  server: { port: 5173 },
})
