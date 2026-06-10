const socket = new WebSocket(`ws://${window.location.host}`);

const lista = document.getElementById('mensagens');
let meuId = null;

socket.onopen = () => {

    adicionarMensagem('Conectado ao servidor');
};

socket.onmessage = (evento) => {

    const dados = JSON.parse(evento.data);

    let texto = '';

    if (dados.tipo === 'identificacao') {
    meuId = dados.cliente;
    return;
}

    // if (dados.tipo === 'chat') {
    //     texto = `Cliente ${dados.cliente}: ${dados.mensagem}`;
    // }
    if (dados.tipo === 'chat') {
    const minhaMensagem = dados.cliente === meuId;

    texto = minhaMensagem
        ? `Você: ${dados.mensagem}`
        : `Cliente ${dados.cliente}: ${dados.mensagem}`;

    adicionarMensagem(texto, minhaMensagem ? 'minha' : 'outro');
    return;
}


    else if (dados.tipo === 'sistema') {
        texto = `[SISTEMA] ${dados.mensagem}`;
    }
    else if (dados.tipo === 'alerta') {
        texto = `[ALERTA] ${dados.mensagem}`;
    }

    adicionarMensagem(texto);
};

socket.onclose = () => {

    adicionarMensagem('Conexão encerrada');
};

function enviar() {

    const campo = document.getElementById('mensagem');

    if (campo.value.trim() === '') {
        return;
    }

    socket.send(campo.value);

    campo.value = '';
}

// function adicionarMensagem(texto) {

//     const li = document.createElement('li');

//     li.textContent = texto;

//     lista.appendChild(li);
// }

function adicionarMensagem(texto, classe = '') {

    const li = document.createElement('li');

    li.textContent = texto;

    if (classe) {
        li.classList.add(classe);
    }

    lista.appendChild(li);
}