const http = require('http');
const fs = require('fs');
const path = require('path');
const WebSocket = require('ws');

const server = http.createServer((req, res) => {

    let filePath = './public/index.html';

    if (req.url !== '/') {
        filePath = './public' + req.url;
    }

    const ext = path.extname(filePath);

    const contentType = {
        '.html': 'text/html',
        '.js': 'application/javascript',
        '.css': 'text/css'
    }[ext] || 'text/plain';

    fs.readFile(filePath, (err, content) => {

        if (err) {
            res.writeHead(404);
            res.end('Arquivo não encontrado');
            return;
        }

        res.writeHead(200, {
            'Content-Type': contentType
        });

        res.end(content);
    });
});

const wss = new WebSocket.Server({ server });

let clientId = 0;
const clientes = new Map();

wss.on('connection', (ws) => {

    clientId++;

    const id = clientId;

    clientes.set(id, ws);

        ws.send(JSON.stringify({
            tipo: 'identificacao',
            cliente: id
        }));

    console.log(`Cliente ${id} conectado`);

    broadcast({
        tipo: 'sistema',
        mensagem: `Cliente ${id} entrou no sistema`
    });

    ws.on('message', (data) => {

        console.log(`Mensagem recebida do cliente ${id}: ${data}`);

        broadcast({
            tipo: 'chat',
            cliente: id,
            mensagem: data.toString()
        });
    });

    ws.on('close', () => {

        console.log(`Cliente ${id} desconectado`);

        clientes.delete(id);

        broadcast({
            tipo: 'sistema',
            mensagem: `Cliente ${id} saiu do sistema`
        });
    });
});

function broadcast(msg) {

    const mensagem = JSON.stringify(msg);

    clientes.forEach((cliente) => {

        if (cliente.readyState === WebSocket.OPEN) {
            cliente.send(mensagem);
        }
    });
}

// setInterval(() => {

//     broadcast({
//         tipo: 'alerta',
//         mensagem: 'Alerta automático do servidor'
//     });

// }, 10000);

server.listen(3000, '0.0.0.0', () => {
    console.log('Servidor rodando em http://localhost:3000');
    console.log('Na rede local, acesse usando http://SEU-IP:3000');
});
