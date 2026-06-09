# Projeto WebSocket - Pedidos em tempo real

Aplicacao Java Web simples para demonstrar comunicacao em tempo real com WebSocket.

## Como executar

1. Abrir o projeto no NetBeans.
2. Executar em um servidor Java EE compativel com WebSocket, como GlassFish.
3. Acessar `http://localhost:8080/WS2/` em duas abas ou dois navegadores.
4. Enviar uma mensagem em uma aba e observar o broadcast chegando nas duas.

## Requisitos atendidos

- Servidor WebSocket em `ws://localhost:8080/WS2/acoes`.
- Interface web em HTML, CSS e JavaScript.
- Envio de mensagens do cliente para o servidor.
- Broadcast do servidor para todos os clientes conectados.
- Exibicao visual das mensagens recebidas.
- Logs no console do servidor para abertura, recebimento e encerramento de conexao.

## Decisoes tecnicas

a) As conexoes dos clientes foram armazenadas em um `ConcurrentHashMap`, usando o ID da sessao como chave.

b) Cada cliente conectado e identificado pelo `Session.getId()` e pelo nome informado na interface.

c) Com polling, cada cliente faria requisicoes HTTP repetidas, aumentando trafego, consumo do servidor e atraso entre uma atualizacao e outra.

d) WebSocket e mais adequado para chats, notificacoes, paineis de pedidos, monitoramento e cenarios que exigem atualizacao em tempo real.

## Formato das mensagens

As mensagens usam JSON para deixar explicita a acao executada e os dados enviados.

Exemplo de envio do cliente:

```json
{"acao":"adicionar","descricao":"Novo pedido #104 recebido"}
```

Exemplo de broadcast do servidor:

```json
{"acao":"comentarioAdicionado","clienteId":"1","nome":"Caixa 1","descricao":"Novo pedido #104 recebido","totalClientes":2}
```
