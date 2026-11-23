# 🐂 Rastro

**Do brinco à venda: controle, previsão e segurança para o gado de corte.**

O Rastro é um sistema de gestão de pecuária de corte com rastreamento dos animais, pensado para o pequeno e médio produtor da região de Gravataí, Glorinha, Morungava e Santo Antônio da Patrulha (RS). Ele acompanha cada animal pelo número do brinco, da compra à venda.

> **Este repositório (`Rastro-TesteIs`) é a versão acadêmica do Rastro**, feita para a disciplina **Implementação de Software** (Análise e Desenvolvimento de Sistemas · Unisinos · 2026). Serve de portfólio e de laboratório: aqui ficam a primeira API e o primeiro front ligados de ponta a ponta. O Rastro completo terá repositório próprio, com mais entidades e front em React + TypeScript.

---

## 📦 O que tem neste repositório

| Pasta | O que é | Tecnologias |
|---|---|---|
| [`rastro-api`](rastro-api/README.md) | API REST: lotes de compra, animais e lotes de venda | Java · Spring Boot · JPA/Hibernate · MySQL · Swagger |
| [`rastro-front`](rastro-front/README.md) | Front-end web que consome a API | Vue 3 · Vite · Axios |

```
Rastro
├── README.md          ← você está aqui
├── LICENSE
├── rastro-api/        ← back-end (porta 8080)
└── rastro-front/      ← front-end (porta 5173)
```

---

## 🔁 Como as partes se ligam

```
Navegador (Vue, localhost:5173)
   │  HTTP + JSON  (axios)
   ▼
rastro-api (Spring Boot, localhost:8080/rastro/api)
   │  Controller → Service → Repository
   ▼
MySQL (rastro_db)
```

---

## 🐄 Regra principal do sistema

| Etapa | Onde acontece | O que muda no animal |
|---|---|---|
| **Entrada** | Lote de compra (cadastrado junto com os animais) | Animal criado como `ATIVO` |
| **Na propriedade** | Tela de animais | Consulta, correção de dados, **baixa** (morte, descarte, outro) |
| **Saída** | Lote de venda (montado selecionando animais ativos) | Animal passa a `VENDIDO` |

A quantidade dos lotes é sempre o número de animais, nunca um número digitado.

---

## 🚀 Rodando tudo

```bash
git clone https://github.com/AquilesFerreira/Rastro-TesteIs.git
cd Rastro-TesteIs
```

1. **Banco:** crie o banco `rastro_db` no MySQL 8 (as tabelas são criadas pelo Hibernate).
2. **API:** siga o [README da API](rastro-api/README.md#-como-rodar) (variável `DB_PASSWORD` + `./mvnw spring-boot:run`). Swagger em http://localhost:8080/swagger-ui.html
3. **Front:** siga o [README do front](rastro-front/README.md#-como-rodar) (`npm install` + `npm run dev`). Abra http://localhost:5173

---

## 🗺️ Situação do projeto

- ✅ API com as 3 entidades centrais, regras de negócio e tratamento de erros
- ✅ Front-end web das 3 entidades
- 🧪 Protótipo de baixa fidelidade testado com usuários (Quant-UX)
- 🔜 Pesagem individual (peso médio × peso real), eventos sanitários, perfis de usuário
- 🔭 Futuro: rastreamento por GPS, mapa e alertas

---

## 👤 Autor

**Aquiles Ferreira** · Unisinos · 2026

## 📄 Licença

Código licenciado sob a **GNU Affero General Public License v3.0 (AGPL-3.0)**. Veja [LICENSE](LICENSE).
O nome **Rastro** e sua identidade visual não estão incluídos nesta licença.
