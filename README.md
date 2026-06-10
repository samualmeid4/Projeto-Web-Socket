# Projeto WebSocket

Projeto simples em Node.js para demonstrar comunicacao em tempo real usando WebSocket. A aplicacao sobe um servidor HTTP, entrega uma pagina web estatica e permite que clientes conectados troquem mensagens em tempo real.

## Funcionalidades

- Servidor HTTP em Node.js.
- Comunicacao WebSocket usando a biblioteca `ws`.
- Interface web simples com campo de mensagem e lista de eventos.
- Mensagens de chat enviadas para todos os clientes conectados.
- Avisos de entrada e saida de clientes.
- Alerta automatico do servidor a cada 10 segundos.

## Tecnologias

- Node.js
- JavaScript
- HTML
- CSS
- Biblioteca [`ws`](https://www.npmjs.com/package/ws)

## Estrutura do projeto

```text
websocket/
  README.md
  web-socket/
    package.json
    package-lock.json
    server.js
    public/
      index.html
      script.js
      style.css
```

## Como executar

Entre na pasta do projeto Node.js:

```bash
cd web-socket
```

Instale as dependencias:

```bash
npm install
```

Inicie o servidor:

```bash
node server.js
```

Depois acesse no navegador:

```text
http://localhost:3000
```

Para testar a comunicacao em tempo real, abra o mesmo endereco em duas abas do navegador e envie mensagens por uma delas.

## Como funciona

O arquivo `server.js` cria um servidor HTTP na porta `3000` e entrega os arquivos da pasta `public`.

Na mesma porta, o servidor tambem cria um servidor WebSocket. Quando um cliente se conecta, ele recebe um ID. As mensagens enviadas por qualquer cliente sao repassadas para todos os clientes conectados.

O front-end, em `public/script.js`, abre uma conexao com:

```js
ws://localhost:3000
```

Depois ele escuta mensagens vindas do servidor e adiciona cada evento na lista exibida na pagina.

## Observacao importante

No `server.js`, a funcao `broadcast` esta sendo chamada, mas no codigo atual ela aparece comentada. Para o envio de mensagens funcionar, essa funcao precisa estar ativa:

```js
function broadcast(msg) {
    const mensagem = JSON.stringify(msg);

    clientes.forEach((cliente) => {
        if (cliente.readyState === WebSocket.OPEN) {
            cliente.send(mensagem);
        }
    });
}
```

Sem essa funcao, o servidor pode gerar erro quando um cliente conectar ou quando o alerta automatico for disparado.

## Scripts disponíveis

Atualmente o `package.json` nao possui um script de inicializacao configurado. Por isso, use:

```bash
node server.js
```

Se quiser adicionar um script de start, inclua no `package.json`:

```json
"scripts": {
  "start": "node server.js"
}
```

Depois disso, o projeto podera ser iniciado com:

```bash
npm start
```
