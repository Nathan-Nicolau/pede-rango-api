# Food Delivery API

Esse projeto de API, desenvolvido com Java 21 + Spring Boot, faz parte do teste técnico solicitado

A API preenche os seguites itens:
* Cadastro de usuários
* Login com email e senha
* Autenticação com JWT

### Funções da Aplicação
* Criação de Pedidos - Podendo ser feito diretamente pelo cliente e pelo Adminstrador
* Cancelamento de Pedidos - Podendo ser feito por qualquer usuário, adicionando um motivo e uma descrição opcional
* Criação de usuários - Usuários cadastrados na tela inicial são obrigatoriamente Clientes

### Somente Adminstrador
- Cadastro de usuários Adminstradores, Entregadores e Clientes  
- Adição de novos Produtos no cardápio

 ### Entidades da Aplicação
 * **Usuário**  
   - Relacionado obrigatoriamente a um tipo de perfil, podendo ser Adminstrador, Entregador ou Cliente
 * **Cliente**
 * **Entregador**
   - Entidade de melhoria, para permitir a relação lógica dos pedidos que deve sair para entrega
 * **Pedido**
 * **Cancelamento Pedido**
   - Entidade de melhoria, para registrar as informações de cancelamento de pedidos de forma separada, permitindo uma consulta de histórico
 * **Item Pedido**
 * **Produto**
   - Essa entidade de melhoria, serve para manter um registro único dos Produtos (itens de menu), tornando mais fácil a  
     criação de Pedidos

**Postgres 16**   
Usado como banco de dados, pela eficiência no gerenciamento de dados para modelos relacionais. É uma das opções mais utilizadas com o ecossistema do Java.  

**Docker**  
Utilizado para gerenciamento da API e do Banco de dados, como containers de aplicação, permitindo a execução do ambiente necessário, sem necessidade de configurações complexas.
Também permite o deploy em produção de forma facilitada.

### Executando
Basta executar o comando abaixo na raiz do projeto, para que a API seja compilada, e o banco seja inicializado

```shell
docker compose up -d --build
```
![Captura de tela de 2026-08-03 22-19-27.png](../../../Imagens/Capturas%20de%20tela/Captura%20de%20tela%20de%202026-08-03%2022-19-27.png)

Pode ser rodado somente o projeto da API, mantendo o container do banco de dados, ou utilizar a opção acima

### Gitflow para versionamento
 - Criação de branchs para Desenvolvimento contínuo, simulando um cenário real.