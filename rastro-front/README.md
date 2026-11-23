# 🐂 Rastro Front

![Vue](https://img.shields.io/badge/Vue-3.5-42b883?logo=vuedotjs&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8-646CFF?logo=vite&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-1.x-5A29E4?logo=axios&logoColor=white)
![Licença](https://img.shields.io/badge/licença-AGPL--3.0-blue)

Front-end web do **Rastro**, feito em **Vue 3 + Vite**. São três telas, **Lotes de compra**, **Animais** e **Lotes de venda**, ligadas à [`rastro-api`](../rastro-api/README.md) (Spring Boot + MySQL).

> Trabalho acadêmico da disciplina **Implementação de Software** · Unisinos · 2026. A arquitetura segue o padrão visto em aula (services + composables + componentes), sem Vue Router e sem Pinia.

---

## 📌 O que dá para fazer

| Tela | Ações |
|---|---|
| **Lotes de compra** | Cadastrar o lote **já com os animais** (uma linha por brinco; a linha nova repete sexo, castrado e raça) · editar o cabeçalho · ver os animais do lote (ativos, vendidos, baixa) · adicionar animal esquecido · remover animal lançado por engano · excluir a compra |
| **Animais** | Consultar o rebanho · filtrar por **Ativos / Vendidos / Baixa / Todos** e por lote de compra · buscar pelo brinco (na API) · editar brinco, sexo, castrado e raça · **dar baixa** (morte, descarte, outro) |
| **Lotes de venda** | Montar a venda **selecionando os animais ativos**, agrupados por lote de compra, com "Marcar todos" · editar · excluir (os animais voltam a ativos) |

A quantidade dos lotes nunca é digitada: é sempre o número de animais.

---

## 🔗 Como o front conversa com o back

```
Tela (components/...Lista.vue)
  → service (services/...Service.js)
    → services/api.js (axios, baseURL do .env)
      → HTTP + JSON
        → Controller → Service → Repository (rastro-api)
          → Hibernate gera o SQL → MySQL
```

O front **nunca fala com o banco**. Ele só chama as URLs da API; as consultas (SQL) nascem nos Repositories do Spring e aparecem no console da API (`spring.jpa.show-sql=true`).

**Exemplo — cadastrar um lote de compra:**
1. O usuário preenche o formulário (`LoteCompraFormulario.vue`) e clica em "Registrar lote com 4 animais".
2. O formulário valida os campos no front (mesmas regras do DTO da API).
3. Chama `cadastrarLoteCompra(dados)` em `loteCompraService.js`.
4. Sai um `POST http://localhost:8080/rastro/api/lotes-compra` com o lote e a lista `animais`.
5. A API valida (`@Valid` + regras do service), salva o lote e os animais numa transação e devolve `201`.
6. A tela fecha o formulário e recarrega a lista (`GET`). Se a API recusar (ex.: brinco repetido), a mensagem dela aparece no formulário.

---

## 🏗️ Estrutura

```
rastro-front
├── .env                     → VITE_API_URL (endereço da API)
├── index.html               → página base
├── vite.config.js           → servidor na porta 5173
└── src
    ├── main.js              → cria o app Vue e carrega o CSS
    ├── App.vue              → menu + troca de tela (variável telaAtual, sem Router)
    ├── assets/base.css      → visual do Rastro (verde-escuro, verde, creme)
    ├── services/            → A LIGAÇÃO COM O BACK
    │   ├── api.js           → instância do axios com a baseURL
    │   ├── loteCompraService.js
    │   ├── animalService.js
    │   └── loteVendaService.js
    ├── composables/         → lógica reaproveitável
    │   ├── useListagem.js   → itens, carregando, erro, vazio
    │   └── useFiltro.js     → filtros da tela e "limpar"
    ├── utils/
    │   ├── formatadores.js  → moeda, kg, data, brinco (BR-0101), rótulos de status
    │   └── erroApi.js       → lê o ApiErrorResponse (message + validationErrors)
    └── components/
        ├── AppHeader.vue, PainelLista.vue, ConfirmarExclusao.vue   → peças comuns
        ├── LoteCompraLista / Card / Formulario / Animais           → tela de compra
        ├── AnimalLista / Card / Formulario, DarBaixa               → tela de animais
        └── LoteVendaLista / Card / Formulario                      → tela de venda
```

Cada tela segue o mesmo padrão: **Lista** (busca os dados e controla os modais) → **Card** (mostra um item e emite eventos) → **Formulario** (cadastra ou edita e devolve `salvo`).

---

## ✅ Validação em duas camadas

| Onde | O quê | Exemplo |
|---|---|---|
| **Front** | Campos obrigatórios, números maiores que zero, brinco repetido no mesmo formulário | "Informe o fornecedor." |
| **API** | As mesmas regras + as que dependem do banco | "Já existe animal com o brinco 101" · "Não é possível excluir o lote: há animais dele vendidos ou com baixa" |

O front valida para dar resposta rápida; a API valida porque é ela quem garante a regra (Swagger, Postman ou qualquer outro cliente também passam por ela). Os erros da API aparecem na tela: a mensagem geral no topo e os `validationErrors` embaixo de cada campo.

---

## 🚀 Como rodar

### Pré-requisitos
- Node.js 20+ (com npm)
- A [`rastro-api`](../rastro-api/README.md) rodando em `http://localhost:8080`

### Passos

```bash
# 1. Na pasta rastro-front, instalar as dependências (só na primeira vez)
npm install

# 2. Criar o arquivo .env (se não existir)
echo VITE_API_URL=http://localhost:8080/rastro/api > .env

# 3. Subir o front
npm run dev
```

Abra **http://localhost:5173**.

> Mudou o `.env`? Pare (`Ctrl+C`) e rode `npm run dev` de novo: o Vite só lê o `.env` ao iniciar. Sem `.env`, o `api.js` usa `http://localhost:8080/rastro/api` como padrão.

### CORS
O navegador só deixa uma página de `localhost:5173` chamar `localhost:8080` se a API permitir. Isso está na classe `CorsConfig` da API (`/rastro/api/**` liberado para `http://localhost:5173`). Swagger e Postman não passam por essa regra porque não são páginas de outra origem.

---

## 🛠️ Tecnologias

Vue 3 (`<script setup>`) · Vite · Axios · JavaScript · CSS puro

---

## 🔭 Próximos passos

- Ficha do animal com **pesagens** (peso médio do lote × peso real) e **eventos sanitários**.
- Paginação quando a API paginar.
- Vue Router quando houver mais telas.
- Visual do protótipo de alta fidelidade.

---

## 👤 Autor

**Aquiles Ferreira**
Implementação de Software · Unisinos · 2026

## 📄 Licença

**GNU Affero General Public License v3.0 (AGPL-3.0)**, a mesma do projeto. Consulte o arquivo [LICENSE](../LICENSE). O nome **Rastro** e sua identidade visual não estão incluídos nesta licença.
