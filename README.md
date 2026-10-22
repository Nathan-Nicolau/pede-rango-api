# Pede Rango API

Esse projeto de API, desenvolvido com Java 21 + Spring Boot, faz parte do teste técnico solicitado e recebeu diversas evoluções técnicas e de regra de negócio em relação à versão base.

A API preenche os seguintes itens base:
* Cadastro de usuários
* Login com email e senha
* Autenticação com JWT

### Funções da Aplicação
* **Criação de Pedidos** - Podendo ser feito diretamente pelo cliente e pelo Administrador, com cálculo automático da taxa de entrega e suporte a formas de pagamento (`DINHEIRO` com cálculo de troco, `PIX` e `CARTAO`).
* **Fluxo completo de Status do Pedido** - Evolução do pedido passando por `RECEBIDO`, `EM_PREPARO`, `PRONTO_PARA_ENTREGA`, `SAIU_PARA_ENTREGA` e `ENTREGUE`.
* **Cancelamento de Pedidos** - Podendo ser feito informando um motivo e uma descrição opcional para registro de auditoria.
* **Criação de usuários** - Usuários cadastrados na tela inicial de auto-cadastro são obrigatoriamente Clientes.
* **Avaliação de Pedidos** - Clientes podem avaliar pedidos já entregues com nota de 1 a 5 estrelas e comentário opcional.

### Somente Administrador e Restaurante
- Cadastro de usuários Administradores, Restaurante, Entregadores e Clientes
- Adição, edição e pausa rápida de Produtos no cardápio (marcar item como esgotado no dia)
- Controle de expediente diário da loja (Iniciar dia, pausar recebimento de pedidos temporariamente e encerrar expediente, com virada automática à meia-noite)
- Consulta de Dashboard e Relatórios com métricas de faturamento, ticket médio, volume de pedidos e média de avaliações

### Entidades da Aplicação
* **Usuário**  
  - Relacionado obrigatoriamente a um tipo de perfil, podendo ser Administrador (`ADMIN`), Restaurante (`RESTAURANTE`), Entregador (`ENTREGADOR`) ou Cliente (`CLIENTE`)
* **Cliente**
* **Entregador**
  - Entidade de melhoria, para permitir a relação lógica dos pedidos que devem sair para entrega e o controle de disponibilidade
* **Pedido** e **Item Pedido**
* **Cancelamento Pedido**
  - Entidade de melhoria, para registrar as informações de cancelamento de pedidos de forma separada, permitindo uma consulta de histórico
* **Produto**
  - Essa entidade de melhoria serve para manter um registro único dos Produtos (itens de menu), tornando mais fácil a criação de Pedidos e o controle do que está ativo no cardápio
* **Configuração Estabelecimento**
  - Entidade de melhoria adicionada para controlar o estado de funcionamento da loja (aberta, pausada ou fechada), taxa de entrega padrão, tempo estimado e chave PIX
* **Avaliação Pedido**
  - Entidade de melhoria adicionada para registrar o feedback e a nota de satisfação do cliente após a entrega do pedido

### Adições Técnicas em relação à versão base

**Flyway (Migrations de Banco de Dados)**  
Adicionado para versionar a criação das tabelas (`V1` e `V2`) e já aplicar a carga inicial de dados (`V3` seed de usuários, cardápio e configurações) automaticamente assim que a aplicação sobe, sem precisar rodar scripts SQL manualmente.

**Spring Boot Actuator & Health Probes**  
Configurado para expor endpoints de saúde (`/actuator/health`, `liveness` e `readiness`), permitindo que o Docker e o Kubernetes saibam exatamente quando a API terminou de iniciar e está pronta para receber tráfego.

**Swagger / OpenAPI**  
Documentação interativa da API (`/swagger-ui.html`) já configurada com suporte para autenticação via token Bearer JWT.

**Postgres 16**   
Usado como banco de dados, pela eficiência no gerenciamento de dados para modelos relacionais. É uma das opções mais utilizadas com o ecossistema do Java.  

**Docker & Kubernetes (Kind + Helm)**  
Utilizado para gerenciamento da API e do Banco de dados como containers de aplicação, permitindo a execução do ambiente necessário sem configurações complexas.  
Além do `compose.yaml` tradicional, o projeto conta com o script `./deploy-local.sh` para empacotar a aplicação, gerar a imagem Docker e publicar diretamente em um cluster Kubernetes local (Kind) gerenciado pelo repositório `pede-rango-infra`.

### Executando

**Opção 1 — Rodando apenas o banco no Docker e a API localmente:**
```shell
docker compose up -d db
./mvnw spring-boot:run
```

**Opção 2 — Rodando tudo via Docker Compose:**
Basta executar o comando abaixo na raiz do projeto, para que a API seja compilada (via Dockerfile multi-stage) e o banco seja inicializado:
```shell
docker compose up -d --build
```

![Captura de tela de 2026-08-03 22-19-27.png](../../../Imagens/Capturas%20de%20tela/Captura%20de%20tela%20de%202026-08-03%2022-19-27.png)

**Opção 3 — Deploy no Cluster Kubernetes Local (Kind):**
Com o cluster ativo (ver repositório `pede-rango-infra`), basta rodar:
```shell
./deploy-local.sh
```

### Credenciais de Acesso Padrão
Ao inicializar a aplicação, o Flyway já cria automaticamente os usuários abaixo para teste de cada perfil (a senha padrão para todos é **`123456`**):

| Perfil | E-mail | Senha | Escopo |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@pederango.com` | `123456` | Acesso total, relatórios, configurações, usuários, entregadores e cardápio |
| **Restaurante** | `restaurante@pederango.com` | `123456` | Gestão de expediente diário, fila de produção e status de itens do cardápio |
| **Entregador** | `entregador@pederango.com` | `123456` | Fila de pedidos atribuídos e atualização de status de entrega |
| **Cliente** | `cliente@pederango.com` | `123456` | Criação de pedidos, histórico, cancelamento e avaliação |

### Gitflow para versionamento
 - Criação de branchs para Desenvolvimento contínuo, simulando um cenário real.
